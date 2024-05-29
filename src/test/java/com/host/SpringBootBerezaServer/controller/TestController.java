package com.host.SpringBootBerezaServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.host.SpringBootBerezaServer.SpringBootBerezaServerApplication;
import com.host.SpringBootBerezaServer.model.Sysstat;
import com.host.SpringBootBerezaServer.service.Nas2HostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = SpringBootBerezaServerApplication.class)
@TestPropertySource(
        locations = "classpath:application-test.properties")
public class TestController {


    @Autowired
    private Nas2HostService nas2HostService; // попробовать передать новый репозиторий через аргументы

    @Autowired
    private NasController nasController;

    @Test
    void test1(){

        int  x = 2;
        assertEquals(2,x);
    }

    @Test
    void test2() throws JsonProcessingException {
        XmlMapper xmlMapper = new XmlMapper();

        String inputParam = "<MESSAGE>\n" +
                "<MSGID>603</MSGID>\n" +
                "<MSGTYPE>SYSSTAT</MSGTYPE>\n" +
                "<REPLYTO>183</REPLYTO>\n" +
                "<TIMESTAMP>20240516115447</TIMESTAMP>\n" +
                "<FACILITY>S</FACILITY>\n" +
                "<ACTION>SET</ACTION>\n" +
                "<SENDER>NAS</SENDER>\n" +
                "<RECIEVER>S</RECIEVER>\n" +
                "<SYSSTAT>\n" +
                "<ERROR_CODE>failed</ERROR_CODE>\n" +
                "<DESCRIPTION> Nepoznata vrsta poruke: ASN_LU</DESCRIPTION>\n" +
                "</SYSSTAT>\n" +
                "</MESSAGE>\n";

        Sysstat systat = nasController.nastohost(inputParam);
        String xml = xmlMapper.writeValueAsString(systat);
        String xmlExpect = xmlMapper.writeValueAsString(new Sysstat(0,"OK"));
        System.out.println(xml);
        System.out.println(xmlExpect);
        assertEquals(xmlExpect, xml);
    }
}
