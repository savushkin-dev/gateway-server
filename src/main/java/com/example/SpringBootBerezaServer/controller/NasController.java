package com.example.SpringBootBerezaServer.controller;

import com.example.SpringBootBerezaServer.model.Sysstat;
import com.example.SpringBootBerezaServer.service.Host2NasService;
import com.example.SpringBootBerezaServer.service.Nas2HostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class NasController {

    private final Nas2HostService nas2HostService;
    private final Host2NasService host2NasService;

    @Autowired
    public NasController(Nas2HostService nas2HostService, Host2NasService host2NasService) {
        this.nas2HostService = nas2HostService;
        this.host2NasService = host2NasService;
    }

    @PostMapping(value = "/nastohost", produces = MediaType.APPLICATION_XML_VALUE)
    public Sysstat nastohost(@RequestBody String requestXML) {
        nas2HostService.saveResponse(requestXML);
        return new Sysstat(0,"OK");
    }

    @PostMapping(value = "/hosttonas", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> hosttonas(@RequestBody String requestXML) {
        return ResponseEntity.ok(host2NasService.SendAndSave(requestXML));
    }

    @PostMapping(value = "/hosttohost", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> hosttohost(@RequestBody String requestXML) {
        return ResponseEntity.ok(host2NasService.SendAndSaveTEST(requestXML));
    }


    //вместо словен
//    @PostMapping(value = "/hosttonasTest")
    @PostMapping(value = "/hosttonasTest", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> hosttonasTest(@RequestBody String str) {

        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<MESSAGE>\n" +
                "  <MSGID>1</MSGID>\n" +
                "  <MSGTYPE>SYSSTAT</MSGTYPE>\n" +
                "  <REPLYTO></REPLYTO>\n" +
                "  <TIMESTAMP>2024-03-29 11:30:00</TIMESTAMP>\n" +
                "  <FACILITY></FACILITY>\n" +
                "  <ACTION>SET</ACTION>\n" +
                "  <SENDER></SENDER>\n" +
                "  <RECIEVER></RECIEVER>\n" +
                "  <SYSSTAT>\n" +
                "    <ERROR_CODE>0</ERROR_CODE>\n" +
                "    <DESCRIPTION>Ok</DESCRIPTION>\n" +
                "  </SYSSTAT>\n" +
                "</MESSAGE>";

        return ResponseEntity.ok(str);
    }

    private String xmlTest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "     <MESSAGE>\n" +
            "       <MSGID>4</MSGID>\n" +
            "       <MSGTYPE>ITEM</MSGTYPE>\n" +
            "       <REPLYTO>t</REPLYTO>\n" +
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
