package com.host.SpringBootBerezaServer.controller;


import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.host.SpringBootBerezaServer.SpringBootBerezaServerApplication;
import com.host.SpringBootBerezaServer.model.Sysstat;
import com.host.SpringBootBerezaServer.repositories.Host2NasRepository;
import com.host.SpringBootBerezaServer.service.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = SpringBootBerezaServerApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties")
public class NasControllerTests {

    private final String TEST_TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVc2VyIGRldGFpbHMiLCJ1c2VybmFtZSI6InRlc3RBY2NvdW50IiwiaWF0IjoxNzE2NzE4NDUyLCJpc3MiOiJTcHJpbmctQmVyZXphLVNlcnZlciIsImV4cCI6MTc0ODI1NDQ1Mn0._6ngENuAIbPKVx1s3xN3oJfHUHbO3VH9WTGNDPN1vh0";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private NasService nasService;


    @Before
    public void before(){
//        host2NasService = new Host2NasService(host2NasRepository, kafkaService, msgNasHostService, nasService);
    }


    @Test
    public void test1() throws Exception {
        XmlMapper xmlMapper = new XmlMapper();

        String inputParam = "<MESSAGE>" +
                "<MSGID>603</MSGID>" +
                "<MSGTYPE>SYSSTAT</MSGTYPE>" +
                "<REPLYTO>183</REPLYTO>" +
                "<TIMESTAMP>20240516115447</TIMESTAMP>" +
                "<FACILITY>S</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>NAS</SENDER>" +
                "<RECIEVER>S</RECIEVER>" +
                "<SYSSTAT>" +
                "<ERROR_CODE>failed</ERROR_CODE>" +
                "<DESCRIPTION> Nepoznata vrsta poruke: ASN_LU</DESCRIPTION>" +
                "</SYSSTAT>" +
                "</MESSAGE>";

//        Sysstat systat = nasController.nastohost(inputParam);
//        String xml = xmlMapper.writeValueAsString(systat);
        String xmlExpect = xmlMapper.writeValueAsString(new Sysstat(0,"OK"));
//        System.out.println(xml);
//        System.out.println(xmlExpect);
//        assertEquals(xmlExpect, xml);

        this.mvc.perform(post("/api/nastohost")
                        .accept(MediaType.APPLICATION_XML)
                        .header("Authorization", TEST_TOKEN)
                        .content(inputParam))
//                .andExpect(status().isOk())
                .andExpect(content().xml(xmlExpect));
    }

    @Test
    public void hosttonasTest1_9() throws Exception {
        XmlMapper xmlMapper = new XmlMapper();

        String reqBody = "<MESSAGE>" +
                "<MSGID>603</MSGID>" +
                "<MSGTYPE>SYSSTAT</MSGTYPE>" +
                "<REPLYTO></REPLYTO>" +
                "<TIMESTAMP>20240516115447</TIMESTAMP>" +
                "<FACILITY>NAS</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>S</SENDER>" +
                "<RECIEVER>NAS</RECIEVER>" +
                "<SYSSTAT>" +
                "<ERROR_CODE>0</ERROR_CODE>" +
                "<DESCRIPTION>ok</DESCRIPTION>" +
                "</SYSSTAT>" +
                "</MESSAGE>";


        given(this.nasService.callNas(reqBody)).willReturn(reqBody);

        this.mvc.perform(post("/api/hosttonas")
                        .accept(MediaType.APPLICATION_XML)
                        .header("Authorization", TEST_TOKEN)
                        .content(reqBody))
                .andExpect(status().isOk());
    }

    @Test
    public void hosttonasTest2_10() throws Exception {

        String reqBody = "<MESSAGE>" +
                "<MSGID></MSGID>" +
                "<MSGTYPE>SYSSTAT</MSGTYPE>" +
                "<REPLYTO></REPLYTO>" +
                "<TIMESTAMP>20240516115447</TIMESTAMP>" +
                "<FACILITY>NAS</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>S</SENDER>" +
                "<RECIEVER>NAS</RECIEVER>" +
                "<SYSSTAT>" +
                "<ERROR_CODE>0</ERROR_CODE>" +
                "<DESCRIPTION>ok</DESCRIPTION>" +
                "</SYSSTAT>" +
                "</MESSAGE>";

        String expResponse = "<Sysstat>" +
                "<description>NasException; Required fields not filled in: MSGID; </description>" +
                "<error_code>400</error_code>" +
                "</Sysstat>";

        given(this.nasService.callNas(reqBody)).willReturn(null);

        this.mvc.perform(post("/api/hosttonas")
                        .accept(MediaType.APPLICATION_XML)
                        .header("Authorization", TEST_TOKEN)
                        .content(reqBody))
                .andExpect(content().string(expResponse))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void hosttonasTest3_11() throws Exception {

        String reqBody = "<MESSAGE>" +
                "<MSGID>603</MSGID>" +
                "<MSGTYPE></MSGTYPE>" +
                "<REPLYTO></REPLYTO>" +
                "<TIMESTAMP>20240516115447</TIMESTAMP>" +
                "<FACILITY>NAS</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>S</SENDER>" +
                "<RECIEVER>NAS</RECIEVER>" +
                "<SYSSTAT>" +
                "<ERROR_CODE>0</ERROR_CODE>" +
                "<DESCRIPTION>ok</DESCRIPTION>" +
                "</SYSSTAT>" +
                "</MESSAGE>";

        String expResponse = "<Sysstat>" +
                "<description>NasException; Required fields not filled in: MSGTYPE; Incorrect Message data; </description>" +
                "<error_code>400</error_code>" +
                "</Sysstat>";

        given(this.nasService.callNas(reqBody)).willReturn(null);

        this.mvc.perform(post("/api/hosttonas")
                        .accept(MediaType.APPLICATION_XML)
                        .header("Authorization", TEST_TOKEN)
                        .content(reqBody))
                .andExpect(content().string(expResponse))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void hosttonasTest4_12() throws Exception {

        String reqBody = "<MESSAGE>" +
                "<MSGID>603</MSGID>" +
                "<MSGTYPE>SYSSTAT</MSGTYPE>" +
                "<REPLYTO></REPLYTO>" +
                "<TIMESTAMP></TIMESTAMP>" +
                "<FACILITY>NAS</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>S</SENDER>" +
                "<RECIEVER>NAS</RECIEVER>" +
                "<SYSSTAT>" +
                "<ERROR_CODE>0</ERROR_CODE>" +
                "<DESCRIPTION>ok</DESCRIPTION>" +
                "</SYSSTAT>" +
                "</MESSAGE>";

        String expResponse = "<Sysstat>" +
                "<description>NasException; Required fields not filled in: TIMESTAMP; Incorrect TIMESTAMP; </description>" +
                "<error_code>400</error_code>" +
                "</Sysstat>";

        given(this.nasService.callNas(reqBody)).willReturn(null);

        this.mvc.perform(post("/api/hosttonas")
                        .accept(MediaType.APPLICATION_XML)
                        .header("Authorization", TEST_TOKEN)
                        .content(reqBody))
                .andExpect(content().string(expResponse))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void hosttonasTest5_13() throws Exception {

        String reqBody = "<MESSAGE>" +
                "<MSGID>603</MSGID>" +
                "<MSGTYPE>SYSSTAT</MSGTYPE>" +
                "<REPLYTO></REPLYTO>" +
                "<TIMESTAMP>2024-05-16 11:54:47</TIMESTAMP>" +
                "<FACILITY>NAS</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>S</SENDER>" +
                "<RECIEVER>NAS</RECIEVER>" +
                "<SYSSTAT>" +
                "<ERROR_CODE>0</ERROR_CODE>" +
                "<DESCRIPTION>ok</DESCRIPTION>" +
                "</SYSSTAT>" +
                "</MESSAGE>";

        String expResponse = "<Sysstat>" +
                "<description>NasException; Incorrect TIMESTAMP; </description>" +
                "<error_code>400</error_code>" +
                "</Sysstat>";

        given(this.nasService.callNas(reqBody)).willReturn(null);

        this.mvc.perform(post("/api/hosttonas")
                        .accept(MediaType.APPLICATION_XML)
                        .header("Authorization", TEST_TOKEN)
                        .content(reqBody))
                .andExpect(content().string(expResponse))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void hosttonasTest6_14() throws Exception {

        String reqBody = "<MESSAGE>" +
                "<MSGID>603</MSGID>" +
                "<MSGTYPE>SYSSTAT</MSGTYPE>" +
                "<REPLYTO></REPLYTO>" +
                "<TIMESTAMP>20240516115447</TIMESTAMP>" +
                "<FACILITY>NAS</FACILITY>" +
                "<ACTION>SET1</ACTION>" +
                "<SENDER>S</SENDER>" +
                "<RECIEVER>NAS</RECIEVER>" +
                "<SYSSTAT>" +
                "<ERROR_CODE>0</ERROR_CODE>" +
                "<DESCRIPTION>ok</DESCRIPTION>" +
                "</SYSSTAT>" +
                "</MESSAGE>";

        String expResponse = "<Sysstat>" +
                "<description>NasException; Incorrect ACTION, must be SET or DELETE; </description>" +
                "<error_code>400</error_code>" +
                "</Sysstat>";

        given(this.nasService.callNas(reqBody)).willReturn(null);

        this.mvc.perform(post("/api/hosttonas")
                        .accept(MediaType.APPLICATION_XML)
                        .header("Authorization", TEST_TOKEN)
                        .content(reqBody))
                .andExpect(content().string(expResponse))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void hosttonasTest7_15() throws Exception {

        String reqBody = "<MESSAGE>" +
                "<MSGID>603</MSGID>" +
                "<MSGTYPE>SYSSTAT1</MSGTYPE>" +
                "<REPLYTO></REPLYTO>" +
                "<TIMESTAMP>20240516115447</TIMESTAMP>" +
                "<FACILITY>NAS</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>S</SENDER>" +
                "<RECIEVER>NAS</RECIEVER>" +
                "<SYSSTAT>" +
                "<ERROR_CODE>0</ERROR_CODE>" +
                "<DESCRIPTION>ok</DESCRIPTION>" +
                "</SYSSTAT>" +
                "</MESSAGE>";

        String expResponse = "<Sysstat>" +
                "<description>NasException; Incorrect Message data; </description>" +
                "<error_code>400</error_code>" +
                "</Sysstat>";

        given(this.nasService.callNas(reqBody)).willReturn(null);

        this.mvc.perform(post("/api/hosttonas")
                        .accept(MediaType.APPLICATION_XML)
                        .header("Authorization", TEST_TOKEN)
                        .content(reqBody))
                .andExpect(content().string(expResponse))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void hosttonasTest8_16() throws Exception {

        String reqBody = "<MESSAGE>" +
                "<MSGID>603</MSGID>" +
                "<MSGTYPE>SYSSTAT</MSGTYPE>" +
                "<REPLYTO></REPLYTO>" +
                "<TIMESTAMP>20240516115447</TIMESTAMP>" +
                "<FACILITY>NAS</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>S</SENDER>" +
                "<RECIEVER>NAS</RECIEVER>" +
                "<SYSSTAT1>" +
                "<ERROR_CODE>0</ERROR_CODE>" +
                "<DESCRIPTION>ok</DESCRIPTION>" +
                "</SYSSTAT1>" +
                "</MESSAGE>";

        String expResponse = "<Sysstat>" +
                "<description>NasException; Incorrect Message data; </description>" +
                "<error_code>400</error_code>" +
                "</Sysstat>";

        given(this.nasService.callNas(reqBody)).willReturn(null);

        this.mvc.perform(post("/api/hosttonas")
                        .accept(MediaType.APPLICATION_XML)
                        .header("Authorization", TEST_TOKEN)
                        .content(reqBody))
                .andExpect(content().string(expResponse))
                .andExpect(status().isBadRequest());
    }

}
