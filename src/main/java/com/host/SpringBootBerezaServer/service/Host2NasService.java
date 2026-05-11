package com.host.SpringBootBerezaServer.service;

import com.host.SpringBootBerezaServer.exceptions.NAS.NasException;
import com.host.SpringBootBerezaServer.model.Host2Nas;
import com.host.SpringBootBerezaServer.model.MsgNasHost;
import com.host.SpringBootBerezaServer.model.Nas2Host;
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
    private final MsgNasHostService msgNasHostService;
    private final NasService nasService;
    private final ModelMapper mapper;


    @Autowired
    public Host2NasService(Host2NasRepository host2NasRepository, NasHostTestRepository nasHostTestRepository, MsgNasHostService msgNasHostService, NasService nasService, ModelMapper mapper) {
        this.host2NasRepository = host2NasRepository;
        this.nasHostTestRepository = nasHostTestRepository;
        this.msgNasHostService = msgNasHostService;
        this.nasService = nasService;
        this.mapper = mapper;
    }


    @Transactional
    public void save(MsgNasHost msgNasHost, boolean isTestReq) {
        if(isTestReq){
            NasHostTest nasHostTest = NasHostTest.convertFromMsgNasHost(msgNasHost);
            nasHostTestRepository.save(nasHostTest);
        } else {
            host2NasRepository.save(Host2Nas.convertFromMsgNasHost(msgNasHost));
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

            if(isTestReq){
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
