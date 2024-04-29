package com.example.SpringBootBerezaServer.service;

import com.example.SpringBootBerezaServer.exceptions.NAS.NasException;
import com.example.SpringBootBerezaServer.exceptions.XMLParsingException;
import com.example.SpringBootBerezaServer.model.Nas2Host;
import com.example.SpringBootBerezaServer.repositories.Nas2HostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
public class Nas2HostService {

//        private static final String FILE_PATH="/srv/logNAS2Host";
    private static final String FILE_PATH = "logNAS2Host";

    private final Nas2HostRepository nas2HostRepository;

    private final KafkaService kafkaService;

    @Autowired
    public Nas2HostService(Nas2HostRepository nas2HostRepository, KafkaService kafkaService) {
        this.nas2HostRepository = nas2HostRepository;
        this.kafkaService = kafkaService;
    }

    public List<Nas2Host> findAll() {
        return nas2HostRepository.findAll();
    }

    @Transactional
    public void save(Nas2Host message) {
        nas2HostRepository.save(message);
    }

    @Transactional(noRollbackFor = RuntimeException.class)
    public void saveResponse(String requestXML) {
        try {
            Nas2Host nas2Host = parsingXML(requestXML, new Nas2Host());

            save(nas2Host);

            kafkaService.sendMessage(requestXML, "NasToHost");

            if (nas2Host.getERRCODE() != 0) {
                throw new NasException(nas2Host.getERRTEXT());
            }

            writeLogN2H(requestXML);
        } catch (Exception ex) {
            log.error(ex.toString());
            writeLogN2H(requestXML, ex.getMessage());
            throw ex;
        }

    }

    public Nas2Host parsingXML(String xml, Nas2Host nas2Host) {
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
                nas2Host.setERRCODE(400);
                StringBuilder textError = new StringBuilder("Required fields not filled in: ");
                for (String x : errorFields) {
                    textError.append(x + "; ");
                }
                nas2Host.setERRTEXT(textError.toString());
            }


            nas2Host = assignFields(nas2Host, map);
            nas2Host.setDATA(xml);


//            System.out.println(host2Nas);
            return nas2Host;

        } catch (XMLStreamException e) {
            throw new XMLParsingException(e.getMessage());
        }


    }

    private Nas2Host assignFields(Nas2Host nas2Host, Map<String, String> map) {
        try {
            nas2Host.setMSGID(map.get("MSGID"));
            nas2Host.setMSGTYPE(map.get("MSGTYPE"));
            nas2Host.setREPLYTO(map.get("REPLYTO"));
            nas2Host.setTIMESTAMP(LocalDateTime.parse(map.get("TIMESTAMP"), Host2NasService.DATE_FORMAT));
            nas2Host.setFACILITY(map.get("FACILITY"));
            nas2Host.setACTION(map.get("ACTION"));
            nas2Host.setSENDER(map.get("SENDER"));
            nas2Host.setRECEIVER(map.get("RECEIVER"));
            nas2Host.setDT(LocalDateTime.now());
        } catch (Exception e) {
//                host2Nas.setERRCODE(400);
            nas2Host.setERRTEXT(nas2Host.getERRTEXT() + " Not all fields are filled in correctly;");
        }
        return nas2Host;
    }


    private String validateTag(XMLEventReader reader) throws XMLStreamException {
        try {
            return reader.nextEvent().asCharacters().getData();
        } catch (ClassCastException e) {
            return "";
        }
    }

    private static void writeLogN2H(String request) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FILE_PATH, true));
            writer.write("Message date - " + LocalDateTime.now() + "\n\n");
            writer.write("Request:" + "\n");
            writer.write(request + "\n\n");
            writer.write("============================================================================");
            writer.write(System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void writeLogN2H(String request, String error) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FILE_PATH, true));
            writer.write("Message date - " + LocalDateTime.now() + "\n\n");
            writer.write("Request:" + "\n");
            writer.write(request + "\n\n");
            writer.write("Error:" + "\n");
            writer.write(error + "\n\n");
            writer.write("============================================================================");
            writer.write(System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
