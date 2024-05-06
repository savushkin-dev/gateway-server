package com.example.SpringBootBerezaServer.service;

import com.example.SpringBootBerezaServer.exceptions.NAS.NasException;
import com.example.SpringBootBerezaServer.exceptions.NAS.NasRemoteServerException;
import com.example.SpringBootBerezaServer.exceptions.XMLParsingException;
import com.example.SpringBootBerezaServer.model.Host2Nas;
import com.example.SpringBootBerezaServer.repositories.Host2NasRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class Host2NasService {

        private static final String FILE_PATH="/srv/logHost2NAS";
//    private static final String FILE_PATH = "logHost2NAS";

    private final String NAS_URL = "http://192.168.10.205:8082/api/hosttonas";

    private final String tokenNas = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6IlNhdnVzaGtpbiIsIm5iZiI6MTcxMTcwNDE0MCwiZXhwIjozMjg5NTQ0NTQwLCJpYXQiOjE3MTE3MDc3NDAsImlzcyI6IkFQUyBkLm8uby4ifQ.8XkzdsBawFhAYrZ8FWVo0QbHmY6pgktvuPf7B_Rq-iI";
    private final String tokenTest = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVc2VyIGRldGFpbHMiLCJ1c2VybmFtZSI6InNhdnVzaGtpbiIsImlhdCI6MTcxMzg3MjkzOCwiaXNzIjoiU3ByaW5nLUJlcmV6YS1TZXJ2ZXIiLCJleHAiOjE3NDU0MDg5Mzh9.maKaKK9maP2eUfSt0nXZlWAOBQOcLeb2lBj_5zHBl3I";


    private final RestTemplate restTemplate;
    private final Host2NasRepository host2NasRepository;
    private final KafkaService kafkaService;


    @Autowired
    public Host2NasService(RestTemplate restTemplate, Host2NasRepository host2NasRepository, KafkaService kafkaService) {
        this.restTemplate = restTemplate;
        this.host2NasRepository = host2NasRepository;
        this.kafkaService = kafkaService;
    }


    public List<Host2Nas> findAll() {
        return host2NasRepository.findAll();
    }

    @Transactional
    public void save(Host2Nas message) {
        host2NasRepository.save(message);
    }



    @Transactional(noRollbackFor = RuntimeException.class)
    public String SendAndSave(String requestXML) {
        long durParsing = -1;
        long durDB = -1;
        long durKafka = -1;


        String responseXML = "-";
        Host2Nas request = new Host2Nas();
        try {

            long startTimeParsing = System.nanoTime();
            request = parsingXML(requestXML, request);
            long endTimeParsing = System.nanoTime();
            durParsing = (endTimeParsing - startTimeParsing);


            long startTimeDB = System.nanoTime();
            save(request);
            long endTimeDB = System.nanoTime();
            durDB = (endTimeDB - startTimeDB);

            long startTimeKafka = System.nanoTime();
            kafkaService.sendMessage(requestXML, "HostToNas");
            long endTimeKafka = System.nanoTime();
            durKafka = (endTimeKafka - startTimeKafka);


            responseXML = callNas(requestXML);


            if (request.getERRCODE() != 0) {
                throw new NasException(request.getERRTEXT());
            }

            writeLogH2N(requestXML, responseXML, durParsing, durDB, durKafka);

            return responseXML + ";";

        } catch (Exception ex) {
            log.error(ex.toString());
            writeLogH2N(requestXML, responseXML, ex.getMessage(), durParsing, durDB, durKafka);
            throw ex;
        }

    }


    private String callNas(String requestXML) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        headers.add("Authorization", tokenNas);
//        headers.add("Authorization", tokenTest);

        HttpEntity<String> request = new HttpEntity<>(requestXML, headers);

        String responseXML = "-";

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(
                    NAS_URL,
//                    "http://localhost:7592/api/hosttonasTest",
                    request, String.class);
            responseXML = response.getBody();
        } catch (Exception e) {
            throw new NasRemoteServerException("Exception when requesting to remote server!" + e.toString(), responseXML);
        }


        return responseXML;
    }


    public Host2Nas parsingXML(String xml, Host2Nas host2Nas) {
        //StAX парсер

        Map<String, String> map = new HashMap<>();
        map.put("MSGID", "");
        map.put("MSGTYPE", "");
        map.put("REPLYTO", "");
        map.put("TIMESTAMP", "");
        map.put("FACILITY", "");
        map.put("ACTION", "");
        map.put("SENDER", "");
        map.put("RECEIVER", "");

        ArrayList<String> requiredFields = new ArrayList<>();
        requiredFields.add("MSGID");
        requiredFields.add("MSGTYPE");
        requiredFields.add("TIMESTAMP");
        requiredFields.add("ACTION");

        try {

            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(new StringReader(xml));


            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();


                    if (startElement.getName().getLocalPart().equals("ITEM")) {
//                        System.out.println(reader.getElementText().isEmpty()); //проверить тег на пустоту
                        break; //выход из потока после окончания заголовка
                    }

                    switch (startElement.getName().getLocalPart()) {
                        case "MSGID" -> map.put("MSGID", validateTag(reader));
                        case "MSGTYPE" -> map.put("MSGTYPE", validateTag(reader));
                        case "REPLYTO" -> map.put("REPLYTO", validateTag(reader));
                        case "TIMESTAMP" -> map.put("TIMESTAMP", validateTag(reader));
                        case "FACILITY" -> map.put("FACILITY", validateTag(reader));
                        case "ACTION" -> map.put("ACTION", validateTag(reader));
                        case "SENDER" -> map.put("SENDER", validateTag(reader));
                        case "RECEIVER" -> map.put("RECEIVER", validateTag(reader));
                    }

                }
            }


            ArrayList<String> errorFields = new ArrayList<>();

            for (String key : map.keySet()) {
                if (requiredFields.contains(key) && map.get(key).isEmpty()) {
                    errorFields.add(key);
                }
            }


            if (!errorFields.isEmpty()) {
                host2Nas.setERRCODE(400);
                StringBuilder textError = new StringBuilder("Required fields not filled in: ");
                for (String x : errorFields) {
                    textError.append(x + "; ");
                }
                host2Nas.setERRTEXT(textError.toString());
            }

            host2Nas = assignFields(host2Nas, map);
            host2Nas.setDATA(xml);


            return host2Nas;

        } catch (XMLStreamException e) {
//            System.out.println("Ошибка XMLStreamException!");
            throw new XMLParsingException(e.toString());
        }


    }

    private Host2Nas assignFields(Host2Nas host2Nas, Map<String, String> map) {
        try {
            host2Nas.setMSGID(map.get("MSGID"));
            host2Nas.setMSGTYPE(map.get("MSGTYPE"));
            host2Nas.setREPLYTO(map.get("REPLYTO"));
            host2Nas.setTIMESTAMP(LocalDateTime.parse(map.get("TIMESTAMP"), Host2NasService.DATE_FORMAT));
            host2Nas.setFACILITY(map.get("FACILITY"));
            host2Nas.setACTION(map.get("ACTION"));
            host2Nas.setSENDER(map.get("SENDER"));
            host2Nas.setRECEIVER(map.get("RECEIVER"));
            host2Nas.setDT(LocalDateTime.now());
        } catch (Exception e) {
//                host2Nas.setERRCODE(400);
            host2Nas.setERRTEXT(host2Nas.getERRTEXT() + " Not all fields are filled in correctly;");
        }
        return host2Nas;
    }

    private String validateTag(XMLEventReader reader) throws XMLStreamException {
        try {
            return reader.nextEvent().asCharacters().getData();
        } catch (ClassCastException e) {
            return "";
        }
    }

    private static void writeLogH2N(String request, String response, long durParsing, long durDB, long durKafka) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FILE_PATH, true));
            writer.write("Message date - " + LocalDateTime.now() + "\n\n");
            writer.write("Duration parsing+validation: " + durParsing + "\n");
            writer.write("Duration save in DB: " + durDB + "\n");
            writer.write("Duration send to kafka: " + durKafka + "\n\n");
            writer.write("Request:" + "\n");
            writer.write(request + "\n\n");
            writer.write("Response:" + "\n");
            writer.write(response + "\n");
            writer.write("============================================================================");
            writer.write(System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    private static void writeLogH2N(String request, String response, String error, long durParsing, long durDB, long durKafka) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FILE_PATH, true));
            writer.write("Message date - " + LocalDateTime.now() + "\n\n");
            writer.write("Duration parsing+validation: " + durParsing + "\n");
            writer.write("Duration save in DB: " + durDB + "\n");
            writer.write("Duration send to kafka: " + durKafka + "\n\n");
            writer.write("Request:" + "\n");
            writer.write(request + "\n\n");
            writer.write("Response:" + "\n");
            writer.write(response + "\n\n");
            writer.write("Error:" + "\n");
            writer.write(error + "\n");
            writer.write("============================================================================");
            writer.write(System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
    }


    public static DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .toFormatter();

}
