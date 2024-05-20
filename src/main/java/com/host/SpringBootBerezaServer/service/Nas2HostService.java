package com.host.SpringBootBerezaServer.service;

import com.host.SpringBootBerezaServer.exceptions.NAS.NasException;
import com.host.SpringBootBerezaServer.model.Nas2Host;
import com.host.SpringBootBerezaServer.repositories.Nas2HostRepository;
import com.host.SpringBootBerezaServer.util.N2HLogging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class Nas2HostService {

    private final Nas2HostRepository nas2HostRepository;

    private final KafkaService kafkaService;

    private final MsgNasHostService msgNasHostService;

    @Autowired
    public Nas2HostService(Nas2HostRepository nas2HostRepository, KafkaService kafkaService, MsgNasHostService msgNasHostService) {
        this.nas2HostRepository = nas2HostRepository;
        this.kafkaService = kafkaService;
        this.msgNasHostService = msgNasHostService;
    }

    @Transactional
    public void save(Nas2Host message) {
        nas2HostRepository.save(message);
    }

    @Transactional(noRollbackFor = RuntimeException.class)
    public void saveResponse(String requestXML) {
        long durParsing = -1;
        long durDB = -1;
        long durKafka = -1;

        try {
            long startTimeParsing = System.nanoTime();
            Nas2Host nas2Host = (Nas2Host) msgNasHostService.parsingXML(requestXML, new Nas2Host());
            long endTimeParsing = System.nanoTime();
            durParsing = (endTimeParsing - startTimeParsing);

            long startTimeDB = System.nanoTime();
            save(nas2Host);
            long endTimeDB = System.nanoTime();
            durDB = (endTimeDB - startTimeDB);


            long startTimeKafka = System.nanoTime();
            kafkaService.sendMessage(requestXML, "NasToHost");
            long endTimeKafka = System.nanoTime();
            durKafka = (endTimeKafka - startTimeKafka);


            if (nas2Host.getERRCODE() != 0) {
                throw new NasException(nas2Host.getERRTEXT());
            }

            N2HLogging.writeLogN2H(requestXML, durParsing, durDB, durKafka);
        } catch (Exception ex) {
            log.error(ex.toString());
            N2HLogging.writeLogN2H(requestXML, ex.toString(), durParsing, durDB, durKafka);
            throw ex;
        }

    }


}
