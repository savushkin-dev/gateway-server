package com.host.SpringBootBerezaServer.service;

import com.host.SpringBootBerezaServer.exceptions.NAS.NasException;
import com.host.SpringBootBerezaServer.model.Nas2Host;
import com.host.SpringBootBerezaServer.repositories.Nas2HostRepository;
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

        private static final String FILE_PATH="/srv/logNAS2Host";
//    private static final String FILE_PATH = "logNAS2Host";

    private final Nas2HostRepository nas2HostRepository;

    private final KafkaService kafkaService;

    private final MsgNasHostService msgNasHostService;

    @Autowired
    public Nas2HostService(Nas2HostRepository nas2HostRepository, KafkaService kafkaService, MsgNasHostService msgNasHostService) {
        this.nas2HostRepository = nas2HostRepository;
        this.kafkaService = kafkaService;
        this.msgNasHostService = msgNasHostService;
    }

    public List<Nas2Host> findAll() {
        return nas2HostRepository.findAll();
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

            writeLogN2H(requestXML, durParsing, durDB, durKafka);
        } catch (Exception ex) {
            log.error(ex.toString());
            writeLogN2H(requestXML, ex.toString(), durParsing, durDB, durKafka);
            throw ex;
        }

    }


    private static void writeLogN2H(String request, long durParsing, long durDB, long durKafka) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FILE_PATH, true));
            writer.write("Message date - " + LocalDateTime.now() + "\n\n");
            writer.write("Duration parsing+validation: " + durParsing + "\n");
            writer.write("Duration save in DB: " + durDB + "\n");
            writer.write("Duration send to kafka: " + durKafka + "\n\n");
            writer.write("Request:" + "\n");
            writer.write(request + "\n\n");
            writer.write("============================================================================");
            writer.write(System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }

    }

    private static void writeLogN2H(String request, String error, long durParsing, long durDB, long durKafka) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FILE_PATH, true));
            writer.write("Message date - " + LocalDateTime.now() + "\n\n");
            writer.write("Duration parsing+validation: " + durParsing + "\n");
            writer.write("Duration save in DB: " + durDB + "\n");
            writer.write("Duration send to kafka: " + durKafka + "\n\n");
            writer.write("Request:" + "\n");
            writer.write(request + "\n\n");
            writer.write("Error:" + "\n");
            writer.write(error + "\n\n");
            writer.write("============================================================================");
            writer.write(System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }

    }

}
