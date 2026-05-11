package com.host.SpringBootBerezaServer.service;

import com.host.SpringBootBerezaServer.exceptions.NAS.NasRemoteServerException;
import com.host.SpringBootBerezaServer.model.connections.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@PropertySource("classpath:static/settings.ini")
public class NasService {

    private final RestTemplate restTemplate;
    private final DirectoryService directoryService;

    @Autowired
    public NasService(RestTemplate restTemplate, DirectoryService directoryService) {
        this.restTemplate = restTemplate;
        this.directoryService = directoryService;
    }

    public String callNas(String requestXML) {

        Connection connection = directoryService.getConnectionDataByKGR("NAS");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);


        headers.add("Authorization", connection.getBearer());

        HttpEntity<String> request = new HttpEntity<>(requestXML, headers);

        String responseXML = "-";

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    connection.getUrl(),
                    request, String.class);
            responseXML = response.getBody();
        } catch (Exception e) {
            throw new NasRemoteServerException("Exception when requesting to remote server! " + e.toString(), responseXML);
        }


        return responseXML;
    }
}
