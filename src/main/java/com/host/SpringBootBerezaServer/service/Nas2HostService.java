package com.host.SpringBootBerezaServer.service;

import com.host.SpringBootBerezaServer.exceptions.NAS.NasException;
import com.host.SpringBootBerezaServer.model.MsgNasHost;
import com.host.SpringBootBerezaServer.model.Nas2Host;
import com.host.SpringBootBerezaServer.model.NasHostTest;
import com.host.SpringBootBerezaServer.repositories.Nas2HostRepository;
import com.host.SpringBootBerezaServer.repositories.NasHostTestRepository;
import com.host.SpringBootBerezaServer.util.N2HLogging;
import com.host.SpringBootBerezaServer.util.N2HTestLogging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class Nas2HostService {

    private final Nas2HostRepository nas2HostRepository;

    private final NasHostTestRepository nasHostTestRepository;

    private final KafkaService kafkaService;

    private final MsgNasHostService msgNasHostService;

    @Autowired
    public Nas2HostService(Nas2HostRepository nas2HostRepository, NasHostTestRepository nasHostTestRepository, KafkaService kafkaService, MsgNasHostService msgNasHostService) {
        this.nas2HostRepository = nas2HostRepository;
        this.nasHostTestRepository = nasHostTestRepository;
        this.kafkaService = kafkaService;
        this.msgNasHostService = msgNasHostService;
    }

    @Transactional
    public void save(MsgNasHost msgNasHost) {
        if(msgNasHostService.testReqCheck(msgNasHost)){
            nasHostTestRepository.save(msgNasHost.convertToNasHostTest());
        } else {
            nas2HostRepository.save(msgNasHost.convertToNat2Host());
        }
    }


    public void sendToKafka(MsgNasHost msgNasHost, String xml) {
        if(msgNasHostService.testReqCheck(msgNasHost)){
            kafkaService.sendMessage(xml, "nhtest");
        } else {
            kafkaService.sendMessage(xml, "NasToHost");
        }
    }

    @Transactional(noRollbackFor = RuntimeException.class)
    public void saveResponse(String requestXML) {
        long durParsing = -1;
        long durDB = -1;
        long durKafka = -1;

        boolean isTest = msgNasHostService.testReqCheck(requestXML);

        try {
            long startTimeParsing = System.nanoTime();
            MsgNasHost msgNasHost = msgNasHostService.parsingXML(requestXML, new MsgNasHost());
            long endTimeParsing = System.nanoTime();
            durParsing = (endTimeParsing - startTimeParsing);


            long startTimeDB = System.nanoTime();
            save(msgNasHost);
            long endTimeDB = System.nanoTime();
            durDB = (endTimeDB - startTimeDB);


            long startTimeKafka = System.nanoTime();
            sendToKafka(msgNasHost, requestXML);
            long endTimeKafka = System.nanoTime();
            durKafka = (endTimeKafka - startTimeKafka);


            if (msgNasHost.getERRCODE() != 0) {
                throw new NasException(msgNasHost.getERRTEXT());
            }


            if(isTest){
                N2HTestLogging.writeLogN2H(requestXML, durParsing, durDB, durKafka);
            } else {
                N2HLogging.writeLogN2H(requestXML, durParsing, durDB, durKafka);
            }

        } catch (Exception ex) {
            log.error(ex.toString());
            if(isTest){
                N2HTestLogging.writeLogN2H(requestXML, durParsing, durDB, durKafka);
            } else {
                N2HLogging.writeLogN2H(requestXML, durParsing, durDB, durKafka);
            }
            throw ex;
        }

    }


}
