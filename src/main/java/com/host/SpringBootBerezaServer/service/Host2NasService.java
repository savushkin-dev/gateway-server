package com.host.SpringBootBerezaServer.service;

import com.host.SpringBootBerezaServer.exceptions.NAS.NasException;
import com.host.SpringBootBerezaServer.exceptions.NAS.NasRemoteServerException;
import com.host.SpringBootBerezaServer.model.Host2Nas;
import com.host.SpringBootBerezaServer.repositories.Host2NasRepository;
import com.host.SpringBootBerezaServer.util.H2NLogging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class Host2NasService {


    private final String NAS_URL = "http://192.168.10.205:8082/api/hosttonas";

    private final String tokenNas = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6IlNhdnVzaGtpbiIsIm5iZiI6MTcxMTcwNDE0MCwiZXhwIjozMjg5NTQ0NTQwLCJpYXQiOjE3MTE3MDc3NDAsImlzcyI6IkFQUyBkLm8uby4ifQ.8XkzdsBawFhAYrZ8FWVo0QbHmY6pgktvuPf7B_Rq-iI";
    private final String tokenTest = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVc2VyIGRldGFpbHMiLCJ1c2VybmFtZSI6InNhdnVzaGtpbiIsImlhdCI6MTcxMzg3MjkzOCwiaXNzIjoiU3ByaW5nLUJlcmV6YS1TZXJ2ZXIiLCJleHAiOjE3NDU0MDg5Mzh9.maKaKK9maP2eUfSt0nXZlWAOBQOcLeb2lBj_5zHBl3I";


    private final RestTemplate restTemplate;
    private final Host2NasRepository host2NasRepository;
    private final KafkaService kafkaService;
    private final MsgNasHostService msgNasHostService;


    @Autowired
    public Host2NasService(RestTemplate restTemplate, Host2NasRepository host2NasRepository, KafkaService kafkaService, MsgNasHostService msgNasHostService) {
        this.restTemplate = restTemplate;
        this.host2NasRepository = host2NasRepository;
        this.kafkaService = kafkaService;
        this.msgNasHostService = msgNasHostService;
    }


    @Transactional
    public void save(Host2Nas message) {
        host2NasRepository.save(message);
    }


    @Transactional(noRollbackFor = RuntimeException.class)
    public String sendAndSave(String requestXML) {
        long durParsing = -1;
        long durDB = -1;
        long durKafka = -1;


        String responseXML = "-";
        Host2Nas request = new Host2Nas();
        try {

            long startTimeParsing = System.nanoTime();
            request = (Host2Nas) msgNasHostService.parsingXML(requestXML, request);
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

            H2NLogging.writeLogH2N(requestXML, responseXML, durParsing, durDB, durKafka);

            return responseXML + ";";

        } catch (Exception ex) {
            log.error(ex.toString());
            H2NLogging.writeLogH2N(requestXML, responseXML, ex.toString(), durParsing, durDB, durKafka);
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
            throw new NasRemoteServerException("Exception when requesting to remote server! " + e.toString(), responseXML);
        }


        return responseXML;
    }


}
