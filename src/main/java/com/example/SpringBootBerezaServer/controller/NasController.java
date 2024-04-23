package com.example.SpringBootBerezaServer.controller;

import com.example.SpringBootBerezaServer.exceptions.NAS.NasRemoteServerException;
import com.example.SpringBootBerezaServer.model.xml.Response;
import com.example.SpringBootBerezaServer.service.Nas2HostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class NasController {


    private final String NAS_URL = "http://192.168.10.205:8082/api/hosttonas";

    private final String tokenNas = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6IlNhdnVzaGtpbiIsIm5iZiI6MTcxMTcwNDE0MCwiZXhwIjozMjg5NTQ0NTQwLCJpYXQiOjE3MTE3MDc3NDAsImlzcyI6IkFQUyBkLm8uby4ifQ.8XkzdsBawFhAYrZ8FWVo0QbHmY6pgktvuPf7B_Rq-iI";

    private final String tokenTest = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVc2VyIGRldGFpbHMiLCJ1c2VybmFtZSI6Im5hcyIsImlhdCI6MTcxMTk1MzkzNCwiaXNzIjoiU3ByaW5nLWFwcCIsImV4cCI6MTc0MzQ4OTkzNH0.5YNa9siSVg8wUgFT95NwdIqVLjpgXvLbx9j7YW96ISI";

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    private final Nas2HostService nas2HostService;

    public NasController(RestTemplate restTemplate, Nas2HostService nas2HostService) {
        this.restTemplate = restTemplate;
        this.nas2HostService = nas2HostService;
    }

    @PostMapping(value = "/nastohost", produces = MediaType.APPLICATION_XML_VALUE)
    public Response nastohost(@RequestBody String str) {

//        System.out.println(str);

        BufferedWriter writer = null;
        try {
//            writer = new BufferedWriter(new FileWriter("logNAS2Host", true));
            writer = new BufferedWriter(new FileWriter("/srv/logNAS2Host", true));
            writer.write("Message date - " + LocalDateTime.now() + "\n\n");
            writer.write(str + "\n");
            writer.write("============================================================================");
            writer.write(System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }

        return new Response("Request processed successfully!");
    }

    @PostMapping(value = "/hosttonas", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> hosttonas(@RequestBody String requestXML) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        headers.add("Authorization", tokenNas);

        HttpEntity<String> request = new HttpEntity<>(requestXML, headers);


        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(NAS_URL,
                    request, String.class);
        } catch (Exception e) {
            writeLog(requestXML, e.getMessage());
            throw new NasRemoteServerException("Exception when requesting to remote server!");
        }

        writeLog(requestXML, response.getBody());

        return ResponseEntity.ok(response.getBody());
    }

    @PostMapping(value = "/hosttohost", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> hosttohost(@RequestBody String requestXML) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        headers.add("Authorization", tokenTest);

        HttpEntity<String> request = new HttpEntity<>(requestXML, headers);


        ResponseEntity<String> response = null;
        try {
            response = restTemplate
                    .exchange("http://localhost:7592/api/hosttonasTest",
//                    .exchange("http://10.35.0.4:7592/api/hosttonasTest",
                            HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            writeLog(requestXML, e.getMessage());
            throw new NasRemoteServerException("Exception when requesting to remote server!");
        }

        System.out.println(nas2HostService.findAll());

        writeLog(requestXML, response.getBody());

        return ResponseEntity.ok(response.getBody());
    }


    //вместо словен
//    @PostMapping(value = "/hosttonasTest")
    @PostMapping(value = "/hosttonasTest", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> hosttonasTest(@RequestBody String str) {

//        System.out.println(request.getHeaders().get("Content-Type"));

//        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                "<MESSAGE>\n" +
//                "  <MSGID>1</MSGID>\n" +
//                "  <MSGTYPE>SYSSTAT</MSGTYPE>\n" +
//                "  <REPLYTO></REPLYTO>\n" +
//                "  <TIMESTAMP>2024-03-29 11:30:00</TIMESTAMP>\n" +
//                "  <FACILITY></FACILITY>\n" +
//                "  <ACTION>SET</ACTION>\n" +
//                "  <SENDER></SENDER>\n" +
//                "  <RECIEVER></RECIEVER>\n" +
//                "  <SYSSTAT>\n" +
//                "    <ERROR_CODE>0</ERROR_CODE>\n" +
//                "    <DESCRIPTION>Ok</DESCRIPTION>\n" +
//                "  </SYSSTAT>\n" +
//                "</MESSAGE>";

        return ResponseEntity.ok(str);
    }


    private static void writeLog(String request, String response) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("logHost2NAS", true));
//            writer = new BufferedWriter(new FileWriter("/srv/logHost2NAS", true));
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


}
