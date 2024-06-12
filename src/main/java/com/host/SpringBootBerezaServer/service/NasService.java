package com.host.SpringBootBerezaServer.service;

import com.host.SpringBootBerezaServer.exceptions.NAS.NasRemoteServerException;
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

    @Value("${nas_url}")
    private String nasUrl;

    @Value("${nas_token}")
    private String nasToken;

    @Value("${test_token}")
    private String testToken;

    private final RestTemplate restTemplate;

    @Autowired
    public NasService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String callNas(String requestXML) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        headers.add("Authorization", nasToken);
//        headers.add("Authorization", testToken);

        HttpEntity<String> request = new HttpEntity<>(requestXML, headers);

        String responseXML = "-";

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    nasUrl,
//                    "http://localhost:7592/api/hosttonasTest",
                    request, String.class);
            responseXML = response.getBody();
        } catch (Exception e) {
            throw new NasRemoteServerException("Exception when requesting to remote server! " + e.toString(), responseXML);
        }


        return responseXML;
    }
}
