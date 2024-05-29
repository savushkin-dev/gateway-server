package com.host.SpringBootBerezaServer.service;

import com.host.SpringBootBerezaServer.exceptions.NAS.NasException;
import com.host.SpringBootBerezaServer.model.Host2Nas;
import com.host.SpringBootBerezaServer.repositories.Host2NasRepository;
import com.host.SpringBootBerezaServer.util.H2NLogging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import com.host.SpringBootBerezaServer.exceptions.NAS.NasRemoteServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@Transactional(readOnly = true)
public class Host2NasService {

    private final Host2NasRepository host2NasRepository;
    private final KafkaService kafkaService;
    private final MsgNasHostService msgNasHostService;
    private final NasService nasService;


    @Autowired
    public Host2NasService(Host2NasRepository host2NasRepository, KafkaService kafkaService, MsgNasHostService msgNasHostService, NasService nasService) {
        this.host2NasRepository = host2NasRepository;
        this.kafkaService = kafkaService;
        this.msgNasHostService = msgNasHostService;
        this.nasService = nasService;
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


            responseXML = nasService.callNas(requestXML);


            if (request.getERRCODE() != 0) {
                throw new NasException(request.getERRTEXT());
            }

            H2NLogging.writeLogH2N(requestXML, responseXML, durParsing, durDB, durKafka);

            return responseXML;

        } catch (Exception ex) {
            log.error(ex.toString());
            H2NLogging.writeLogH2N(requestXML, responseXML, ex.toString(), durParsing, durDB, durKafka);
            throw ex;
        }
    }


}
