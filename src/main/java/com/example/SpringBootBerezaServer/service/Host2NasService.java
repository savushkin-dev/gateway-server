package com.example.SpringBootBerezaServer.service;

import com.example.SpringBootBerezaServer.exceptions.XMLParsingException;
import com.example.SpringBootBerezaServer.model.Host2Nas;
import com.example.SpringBootBerezaServer.model.Nas2Host;
import com.example.SpringBootBerezaServer.repositories.Host2NasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class Host2NasService {

    @Autowired
    private final Host2NasRepository host2NasRepository;

    public Host2NasService(Host2NasRepository host2NasRepository) {
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
    public void ParseAndSave(String xml){
        Host2Nas host2Nas = parsingXML(xmlTest);
        host2NasRepository.save(host2Nas);
    }

//    private Host2Nas validate(Host2Nas host2Nas){
////        if (host2Nas.getMSGID().isEmpty()
////        ||host2Nas.getMSGTYPE().isEmpty()
////                ||host2Nas.getTIMESTAMP().isEmpty())
//    }

    public Host2Nas parsingXML(String xml){
        //StAX парсер
        String MSGID = "", MSGTYPE = "", REPLYTO = "", TIMESTAMP = "", FACILITY = "", ACTION = "", SENDER = "", RECEIVER = "";
        boolean messageData = true;

        try {

            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(new StringReader(xml));


            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();

//                    if (startElement.getName().getLocalPart().equals("ITEM")) {
//                        break; //выход из потока после окончания заголовка
//                    }

                    if (startElement.getName().getLocalPart().equals("ITEM")) {
//                        System.out.println(reader.nextEvent().); //проверить тег на пустоту
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
                throw new XMLParsingException("Required fields are not filled in!");
            }

            Host2Nas host2Nas = new Host2Nas(MSGID,MSGTYPE,REPLYTO, LocalDateTime.parse(TIMESTAMP, Host2NasService.DATE_FORMAT) ,FACILITY,ACTION,SENDER,RECEIVER);
            host2Nas.setDATA(xml);


            System.out.println(host2Nas);
            return host2Nas;

        } catch (XMLStreamException e) {
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

    public static DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .toFormatter();


    private String xmlTest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "     <MESSAGE>\n" +
            "       <MSGID>4</MSGID>\n" +
            "       <MSGTYPE>ITEM</MSGTYPE>\n" +
            "       <REPLYTO></REPLYTO>\n" +
            "       <TIMESTAMP>2024-04-22 14:30:01</TIMESTAMP>\n" +
            "       <FACILITY>test</FACILITY>\n" +
            "       <ACTION>SET</ACTION>\n" +
            "       <SENDER>HOST</SENDER>\n" +
            "       <RECIEVER>NAS</RECIEVER>\n" +
            "         <ITEM>\n" +
            "           <ITEM_ID>0204210168</ITEM_ID>\n" +
            "           <ITEM_REF>2402</ITEM_ID>\n" +
            "           <SKU_UOM/>\n" +
            "           <BASIC_UOM>PCE</BASIC_UOM>\n" +
            "           <SKU_BASIC_QUANTITY/>\n" +
            "           <NAME>Сырп/твБЛфин45%фас.н-бр.200г</NAME>\n" +
            "           <EAN>4810268035258</EAN>\n" +
            "           <CATEGORY>0201010000</CATEGORY>\n" +
            "           <CATEGORY_NAME>Категории и сегменты</CATEGORY_NAME>\n" +
            "           <NAS/>\n" +
            "           <DESCRIPTION>Сыр полутвердый \"Брест-Литовск финский\" массовой долей жира в сухом веществе 45 % фасованный (нарезка-брусок) 200 г</DESCRIPTION>\n" +
            "           <SPECIFICATION/>\n" +
            "           <ACTIVE/>\n" +
            "           <QUALITY_CONTROL/>\n" +
            "           <NETTO_WEIGHT/>\n" +
            "           <BRUTTO_WEIGHT>0.2</BRUTTO_WEIGHT>>\n" +
            "           <VOLUME/>\n" +
            "           <SHELF_LIFE/>\n" +
            "           <FREQUENCY/>\n" +
            "           <LOT/>\n" +
            "           <BBDATE/>\n" +
            "           <SERIAL/>\n" +
            "           <WRAPPING/>\n" +
            "           <LU_TYPE/>\n" +
            "           <PACKINGS/>\n" +
            "           <TEMPERATURE_REGIME/>\n" +
            "         </ITEM>\n" +
            "     </MESSAGE>\n";
}
