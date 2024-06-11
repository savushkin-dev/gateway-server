package com.host.SpringBootBerezaServer.service;

import com.host.SpringBootBerezaServer.exceptions.NAS.NasException;
import com.host.SpringBootBerezaServer.model.Host2Nas;
import com.host.SpringBootBerezaServer.model.MsgNasHost;
import com.host.SpringBootBerezaServer.model.NasHostTest;
import com.host.SpringBootBerezaServer.repositories.Host2NasRepository;
import com.host.SpringBootBerezaServer.repositories.NasHostTestRepository;
import com.host.SpringBootBerezaServer.util.H2NLogging;
import com.host.SpringBootBerezaServer.util.H2NTestLogging;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class Host2NasService {

    private final Host2NasRepository host2NasRepository;
    private final NasHostTestRepository nasHostTestRepository;
    private final KafkaService kafkaService;
    private final MsgNasHostService msgNasHostService;
    private final NasService nasService;
    private final ModelMapper mapper;


    @Autowired
    public Host2NasService(Host2NasRepository host2NasRepository, NasHostTestRepository nasHostTestRepository, KafkaService kafkaService, MsgNasHostService msgNasHostService, NasService nasService, ModelMapper mapper) {
        this.host2NasRepository = host2NasRepository;
        this.nasHostTestRepository = nasHostTestRepository;
        this.kafkaService = kafkaService;
        this.msgNasHostService = msgNasHostService;
        this.nasService = nasService;
        this.mapper = mapper;
    }


    @Transactional
    public void save(MsgNasHost msgNasHost, boolean isTestReq) {
        if(isTestReq){
            nasHostTestRepository.save(mapper.map(msgNasHost, NasHostTest.class));
        } else {
            host2NasRepository.save(mapper.map(msgNasHost, Host2Nas.class));
        }
    }

    public void sendToKafka(String xml, boolean isTestReq) {
        if(isTestReq){
            kafkaService.sendMessage(xml, "nhtest");
        } else {
            kafkaService.sendMessage(xml, "HostToNas");
        }
    }


    @Transactional(noRollbackFor = RuntimeException.class)
    public String sendAndSave(String requestXML) {
        long durParsing = -1;
        long durDB = -1;
        long durKafka = -1;

        boolean isTestReq = msgNasHostService.testReqCheck(requestXML);
        String responseXML = "-";

        try {

            long startTimeParsing = System.nanoTime();
            MsgNasHost msgNasHost = msgNasHostService.parsingXML(requestXML, new MsgNasHost());
            long endTimeParsing = System.nanoTime();
            durParsing = (endTimeParsing - startTimeParsing);


            long startTimeDB = System.nanoTime();
            save(msgNasHost, isTestReq);
            long endTimeDB = System.nanoTime();
            durDB = (endTimeDB - startTimeDB);


            long startTimeKafka = System.nanoTime();
            sendToKafka(requestXML, isTestReq);
            long endTimeKafka = System.nanoTime();
            durKafka = (endTimeKafka - startTimeKafka);


            if(msgNasHostService.testReqCheck(msgNasHost)){
                responseXML = "-";
            } else {
                responseXML = nasService.callNas(requestXML);
            }


            if (msgNasHost.getERRCODE() != 0) {
                throw new NasException(msgNasHost.getERRTEXT());
            }


            if(isTestReq){
                H2NTestLogging.writeLogH2N(requestXML, responseXML, durParsing, durDB, durKafka);
            } else {
                H2NLogging.writeLogH2N(requestXML, responseXML, durParsing, durDB, durKafka);
            }

            return responseXML;

        } catch (Exception ex) {
            log.error(ex.toString());
            if(isTestReq){
                H2NTestLogging.writeLogH2N(requestXML, responseXML, ex.toString(), durParsing, durDB, durKafka);
            } else {
                H2NLogging.writeLogH2N(requestXML, responseXML, ex.toString(), durParsing, durDB, durKafka);
            }
            throw ex;
        }
    }



}
