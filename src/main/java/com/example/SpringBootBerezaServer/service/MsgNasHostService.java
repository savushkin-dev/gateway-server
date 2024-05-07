package com.example.SpringBootBerezaServer.service;

import com.example.SpringBootBerezaServer.exceptions.XMLParsingException;
import com.example.SpringBootBerezaServer.model.MsgNasHost;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class MsgNasHostService {


    public MsgNasHost parsingXML(String xml, MsgNasHost obj) {
        //StAX парсер

        boolean containsMsgData = false;

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


                    if (startElement.getName().getLocalPart().equals("ITEM") ||
                            startElement.getName().getLocalPart().equals("SYSSTAT")) {
                        containsMsgData = true;
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

            obj.setERRTEXT("");

            if(!containsMsgData){
                obj.setERRCODE(400);
                obj.setERRTEXT(obj.getERRTEXT() + "Message data is empty; ");
            }


            ArrayList<String> errorFields = new ArrayList<>();

            for (String key : map.keySet()) {
                if (requiredFields.contains(key) && map.get(key).isEmpty()) {
                    errorFields.add(key);
                }
            }


            if (!errorFields.isEmpty()) {
                obj.setERRCODE(400);
                StringBuilder textError = new StringBuilder("Required fields not filled in: ");
                for (String x : errorFields) {
                    textError.append(x + "; ");
                }
                obj.setERRTEXT(textError.toString());
            }


            obj = assignFields(obj, map);
            obj.setDATA(xml);


            return obj;

        } catch (XMLStreamException e) {
            throw new XMLParsingException(e.toString());
        }
    }

    private MsgNasHost assignFields(MsgNasHost obj, Map<String, String> map) {
        try {
            obj.setMSGID(map.get("MSGID"));
            obj.setMSGTYPE(map.get("MSGTYPE"));
            obj.setREPLYTO(map.get("REPLYTO"));
            obj.setTIMESTAMP(LocalDateTime.parse(map.get("TIMESTAMP"), MsgNasHostService.DATE_FORMAT));
            obj.setFACILITY(map.get("FACILITY"));
            obj.setACTION(map.get("ACTION"));
            obj.setSENDER(map.get("SENDER"));
            obj.setRECEIVER(map.get("RECEIVER"));
            obj.setDT(LocalDateTime.now());
        } catch (Exception e) {
//                obj.setERRCODE(400);
            obj.setERRTEXT(obj.getERRTEXT() + "Not all fields are filled in correctly; ");
        }
        return obj;
    }


    private String validateTag(XMLEventReader reader) throws XMLStreamException {
        try {
            return reader.nextEvent().asCharacters().getData();
        } catch (ClassCastException e) {
            return "";
        }
    }

    public static DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .toFormatter();

}
