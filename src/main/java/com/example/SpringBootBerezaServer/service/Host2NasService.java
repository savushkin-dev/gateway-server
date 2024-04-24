package com.example.SpringBootBerezaServer.service;

import com.example.SpringBootBerezaServer.exceptions.NAS.NasRemoteServerException;
import com.example.SpringBootBerezaServer.exceptions.XMLParsingException;
import com.example.SpringBootBerezaServer.model.Host2Nas;
import com.example.SpringBootBerezaServer.repositories.Host2NasRepository;
import com.example.SpringBootBerezaServer.repositories.Nas2HostRepository;
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
import java.util.List;

@Service
@Transactional(readOnly = true)
public class Host2NasService {

        private static final String FILE_PATH="/srv/logHost2NAS";
//    private static final String FILE_PATH="logHost2NAS";

    private final String NAS_URL = "http://192.168.10.205:8082/api/hosttonas";

    private final String tokenNas = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6IlNhdnVzaGtpbiIsIm5iZiI6MTcxMTcwNDE0MCwiZXhwIjozMjg5NTQ0NTQwLCJpYXQiOjE3MTE3MDc3NDAsImlzcyI6IkFQUyBkLm8uby4ifQ.8XkzdsBawFhAYrZ8FWVo0QbHmY6pgktvuPf7B_Rq-iI";

    private final String tokenTest = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVc2VyIGRldGFpbHMiLCJ1c2VybmFtZSI6InNhdnVzaGtpbiIsImlhdCI6MTcxMzg3MjkzOCwiaXNzIjoiU3ByaW5nLUJlcmV6YS1TZXJ2ZXIiLCJleHAiOjE3NDU0MDg5Mzh9.maKaKK9maP2eUfSt0nXZlWAOBQOcLeb2lBj_5zHBl3I";


    private final RestTemplate restTemplate;

    private final Host2NasRepository host2NasRepository;


    @Autowired
    public Host2NasService(RestTemplate restTemplate, Host2NasRepository host2NasRepository) {
        this.restTemplate = restTemplate;
        this.host2NasRepository = host2NasRepository;
    }


    public List<Host2Nas> findAll() {
        return host2NasRepository.findAll();
    }

    @Transactional
    public void save(Host2Nas message){
        host2NasRepository.save(message);
    }

    @Transactional
    public String SendAndSave(String requestXML){
        Host2Nas request = parsingXML(requestXML);
        String responseXML = sendToNasAndWriteLog(requestXML);
        Host2Nas response = parsingXML(responseXML);

        save(request);
        save(response);
        writeLogH2N(requestXML, responseXML);
        return responseXML;
    }

    @Transactional
    public String SendAndSaveTEST(String requestXML){
        Host2Nas request = parsingXML(requestXML);
        String responseXML = sendToNasAndWriteLogTEST(requestXML);
        Host2Nas response = parsingXML(responseXML);

        save(request);
        save(response);
        writeLogH2N(requestXML, responseXML);
        return responseXML;
    }

    private String sendToNasAndWriteLog(String requestXML){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        headers.add("Authorization", tokenNas);

        HttpEntity<String> request = new HttpEntity<>(requestXML, headers);


        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(NAS_URL,
                    request, String.class);
        } catch (Exception e) {
            writeLogH2N(requestXML, response.getBody(), e.getMessage());
            throw new NasRemoteServerException("Exception when requesting to remote server!");
        }



        return response.getBody();
    }

    private String sendToNasAndWriteLogTEST(String requestXML){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        headers.add("Authorization", tokenTest);

        HttpEntity<String> request = new HttpEntity<>(requestXML, headers);


        ResponseEntity<String> response = null;
        try {
            response = restTemplate
//                    .exchange("http://localhost:7592/api/hosttonasTest",
                    .exchange("http://10.35.0.4:7592/api/hosttonasTest",
                            HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            writeLogH2N(requestXML, response.getBody(), e.getMessage());
            throw new NasRemoteServerException("Exception when requesting to remote server!");
        }


        return response.getBody();
    }


    public Host2Nas parsingXML(String xml){
        //StAX парсер
        String MSGID = "", MSGTYPE = "", REPLYTO = "", TIMESTAMP = "", FACILITY = "", ACTION = "", SENDER = "", RECEIVER = "";

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
                        case "MSGID" -> MSGID = validateTag(reader);
                        case "MSGTYPE" -> MSGTYPE = validateTag(reader);
                        case "REPLYTO" -> REPLYTO = validateTag(reader);
                        case "TIMESTAMP" -> TIMESTAMP = validateTag(reader);
                        case "FACILITY" -> FACILITY = validateTag(reader);
                        case "ACTION" -> ACTION = validateTag(reader);
                        case "SENDER" -> SENDER = validateTag(reader);
                        case "RECEIVER" -> RECEIVER = validateTag(reader);
                    }

                }
            }

            if(MSGID.isEmpty() || MSGTYPE.isEmpty() || TIMESTAMP.isEmpty() || ACTION.isEmpty() ){
                writeLogH2N(xml, "-","XMLParsingException - Required fields are not filled in!");
                throw new XMLParsingException("Required fields are not filled in!");
            }

            Host2Nas host2Nas = new Host2Nas(MSGID,MSGTYPE,REPLYTO, LocalDateTime.parse(TIMESTAMP,
                    Host2NasService.DATE_FORMAT) ,FACILITY,ACTION,SENDER,RECEIVER);
            host2Nas.setDATA(xml);

//            System.out.println(host2Nas);
            return host2Nas;

        } catch (XMLStreamException e) {
            writeLogH2N(xml, "-","XMLParsingException - " + e.getMessage());
            throw new XMLParsingException(e.getMessage());
        }


    }

    public Host2Nas parsingNAS(String xml){
        //StAX парсер
        String MSGID = "", MSGTYPE = "", REPLYTO = "", TIMESTAMP = "", FACILITY = "", ACTION = "", SENDER = "", RECEIVER = "";

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
                        case "MSGID" -> MSGID = validateTag(reader);
                        case "MSGTYPE" -> MSGTYPE = validateTag(reader);
                        case "REPLYTO" -> REPLYTO = validateTag(reader);
                        case "TIMESTAMP" -> TIMESTAMP = validateTag(reader);
                        case "FACILITY" -> FACILITY = validateTag(reader);
                        case "ACTION" -> ACTION = validateTag(reader);
                        case "SENDER" -> SENDER = validateTag(reader);
                        case "RECEIVER" -> RECEIVER = validateTag(reader);
                    }

                }
            }

            if(MSGID.isEmpty() || MSGTYPE.isEmpty() || TIMESTAMP.isEmpty() || ACTION.isEmpty() ){
                writeLogH2N(xml, "-","XMLParsingException - Required fields are not filled in!");
                throw new XMLParsingException("Required fields are not filled in!");
            }

            Host2Nas host2Nas = new Host2Nas(MSGID,MSGTYPE,REPLYTO, LocalDateTime.parse(TIMESTAMP,
                    Host2NasService.DATE_FORMAT) ,FACILITY,ACTION,SENDER,RECEIVER);
            host2Nas.setDATA(xml);

//            System.out.println(host2Nas);
            return host2Nas;

        } catch (XMLStreamException e) {
            writeLogH2N(xml, "-","XMLParsingException - " + e.getMessage());
            throw new XMLParsingException(e.getMessage());
        }


    }

    private String validateTag(XMLEventReader reader) throws XMLStreamException {
        try {
            return reader.nextEvent().asCharacters().getData();
        } catch (ClassCastException e) {
            return "";
        }
    }

    private static void writeLogH2N(String request, String response) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FILE_PATH, true));
            writer.write("Message date - " + LocalDateTime.now() + "\n\n");
            writer.write("Request:" + "\n");
            writer.write(request + "\n\n");
            writer.write("Response:" + "\n");
            writer.write(response + "\n");
            writer.write("============================================================================");
            writer.write(System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeLogH2N(String request, String response, String error) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FILE_PATH, true));
            writer.write("Message date - " + LocalDateTime.now() + "\n\n");
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
            throw new RuntimeException(e);
        }
    }


    public static DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .toFormatter();

}
