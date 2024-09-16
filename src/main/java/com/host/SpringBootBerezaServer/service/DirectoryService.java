package com.host.SpringBootBerezaServer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.host.SpringBootBerezaServer.model.connections.Connection;
import com.host.SpringBootBerezaServer.model.connections.NsGrNmsg;
import com.host.SpringBootBerezaServer.repositories.GRNMSGRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DirectoryService {

    public static Map<String, NsGrNmsg> NS_GRNMSG_MAP = new HashMap<>();

    private final GRNMSGRepository grnmsgRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public DirectoryService(GRNMSGRepository grnmsgRepository, ObjectMapper objectMapper) {
        this.grnmsgRepository = grnmsgRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void postConstruct() {
        for (NsGrNmsg obj : grnmsgRepository.findAll()) {
            NS_GRNMSG_MAP.put(obj.getKgr().trim(), obj);
        }
    }

    public Connection getConnectionDataByKGR(String name){

        NsGrNmsg obj = NS_GRNMSG_MAP.get(name);

        try {
           return  objectMapper.readValue(obj.getConnection(), Connection.class);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сопоставления со справочником NS_GRNMSG!");
            throw new RuntimeException("Ошибка сопоставления со справочником NS_GRNMSG!");
        }
    }



}
