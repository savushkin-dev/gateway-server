package com.example.SpringBootBerezaServer.service;

import com.example.SpringBootBerezaServer.exceptions.XMLParsingException;
import com.example.SpringBootBerezaServer.model.Nas2Host;
import com.example.SpringBootBerezaServer.repositories.Nas2HostRepository;
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
import java.util.List;

@Service
@Transactional(readOnly = true)
public class Nas2HostService {

//    private static final String FILE_PATH="/srv/logNAS2Host";
    private static final String FILE_PATH="logNAS2Host";

    private final Nas2HostRepository nas2HostRepository;

    @Autowired
    public Nas2HostService(Nas2HostRepository nas2HostRepository) {
        this.nas2HostRepository = nas2HostRepository;
    }

    public List<Nas2Host> findAll(){
        return nas2HostRepository.findAll();
    }


    public void save(Nas2Host message){
      nas2HostRepository.save(message);
    }


    public void saveResponse(String requestXML){
        Nas2Host nas2Host = parsingXML(requestXML);
        save(nas2Host);
        writeLogN2H(requestXML);
    }

    public Nas2Host parsingXML(String xml){
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

            if(MSGID.isEmpty() || MSGTYPE.isEmpty() || TIMESTAMP.isEmpty() || ACTION.isEmpty() || REPLYTO.isEmpty() ){
                writeLogN2H(xml, "XMLParsingException - Required fields are not filled in!");
                throw new XMLParsingException("Required fields are not filled in!");
            }

            Nas2Host nas2Host = new Nas2Host(MSGID,MSGTYPE,REPLYTO, LocalDateTime.parse(TIMESTAMP,
                    Host2NasService.DATE_FORMAT) ,FACILITY,ACTION,SENDER,RECEIVER);
            nas2Host.setDATA(xml);

//            System.out.println(host2Nas);
            return nas2Host;

        } catch (XMLStreamException e) {
            writeLogN2H(xml, "XMLParsingException - " + e.getMessage());
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
