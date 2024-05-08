package com.host.SpringBootBerezaServer.controller;

import com.host.SpringBootBerezaServer.model.Sysstat;
import com.host.SpringBootBerezaServer.service.Host2NasService;
import com.host.SpringBootBerezaServer.service.Nas2HostService;
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
        return ResponseEntity.ok(host2NasService.sendAndSave(requestXML));
    }

//    @PostMapping(value = "/hosttohost", produces = MediaType.APPLICATION_XML_VALUE)
//    public ResponseEntity<?> hosttohost(@RequestBody String requestXML) {
//        return ResponseEntity.ok(host2NasService.SendAndSaveTEST(requestXML));
//    }



    @PostMapping(value = "/hosttonasTest", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> hosttonasTest(@RequestBody String str) {

        return ResponseEntity.ok(str);
//        return ResponseEntity.badRequest().body(str);
    }



}
