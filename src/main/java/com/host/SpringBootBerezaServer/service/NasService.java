package com.host.SpringBootBerezaServer.service;

import com.host.SpringBootBerezaServer.exceptions.NAS.NasRemoteServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NasService {

    private final String NAS_URL = "http://192.168.10.205:8082/api/hosttonas";

    private final String tokenNas = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6IlNhdnVzaGtpbiIsIm5iZiI6MTcxMTcwNDE0MCwiZXhwIjozMjg5NTQ0NTQwLCJpYXQiOjE3MTE3MDc3NDAsImlzcyI6IkFQUyBkLm8uby4ifQ.8XkzdsBawFhAYrZ8FWVo0QbHmY6pgktvuPf7B_Rq-iI";
    private final String tokenTest = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVc2VyIGRldGFpbHMiLCJ1c2VybmFtZSI6InNhdnVzaGtpbiIsImlhdCI6MTcxMzg3MjkzOCwiaXNzIjoiU3ByaW5nLUJlcmV6YS1TZXJ2ZXIiLCJleHAiOjE3NDU0MDg5Mzh9.maKaKK9maP2eUfSt0nXZlWAOBQOcLeb2lBj_5zHBl3I";

    private final RestTemplate restTemplate;

    @Autowired
    public NasService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String callNas(String requestXML) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

//        headers.add("Authorization", tokenNas);
        headers.add("Authorization", tokenTest);

        HttpEntity<String> request = new HttpEntity<>(requestXML, headers);

        String responseXML = "-";

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(
//                    NAS_URL,
                    "http://localhost:7592/api/hosttonasTest",
                    request, String.class);
            responseXML = response.getBody();
        } catch (Exception e) {
            throw new NasRemoteServerException("Exception when requesting to remote server! " + e.toString(), responseXML);
        }


        return responseXML;
    }
}
