package com.host.SpringBootBerezaServer.controller;

import com.host.SpringBootBerezaServer.SpringBootBerezaServerApplication;
import com.host.SpringBootBerezaServer.configuration.TestSecurityConfig;
import com.host.SpringBootBerezaServer.service.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
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
@Import(TestSecurityConfig.class)
class NasControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private NasService nasService;


    @Test
    public void nastohostTest1_1() throws Exception {

        String inputParam = "<MESSAGE>" +
                "<MSGID>603</MSGID>" +
                "<MSGTYPE>SYSSTAT</MSGTYPE>" +
                "<REPLYTO>183</REPLYTO>" +
                "<TIMESTAMP>20240516115447</TIMESTAMP>" +
                "<FACILITY>TEST</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>NAS</SENDER>" +
                "<RECIEVER>TEST</RECIEVER>" +
                "<SYSSTAT>" +
                "<ERROR_CODE>failed</ERROR_CODE>" +
                "<DESCRIPTION> Nepoznata vrsta poruke: ASN_LU</DESCRIPTION>" +
                "</SYSSTAT>" +
                "</MESSAGE>";

        String expResponse = "<Sysstat>" +
                "<description>OK</description>" +
                "<error_code>0</error_code>" +
                "</Sysstat>";



        this.mvc.perform(post("/api/nastohost")
                        .accept(MediaType.APPLICATION_XML)
                        .content(inputParam))
                .andExpect(content().xml(expResponse))
                .andExpect(status().isOk());
    }

    @Test
    public void nastohostTest2_2() throws Exception {

        String inputParam = "<MESSAGE>" +
                "<MSGID></MSGID>" +
                "<MSGTYPE>SYSSTAT</MSGTYPE>" +
                "<REPLYTO>183</REPLYTO>" +
                "<TIMESTAMP>20240516115447</TIMESTAMP>" +
                "<FACILITY>TEST</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>NAS</SENDER>" +
                "<RECIEVER>TEST</RECIEVER>" +
                "<SYSSTAT>" +
                "<ERROR_CODE>0</ERROR_CODE>" +
                "<DESCRIPTION>ok</DESCRIPTION>" +
                "</SYSSTAT>" +
                "</MESSAGE>";

        String expResponse = "<Sysstat>" +
                "<description>NasException; Required fields not filled in: MSGID; </description>" +
                "<error_code>400</error_code>" +
                "</Sysstat>";

        this.mvc.perform(post("/api/nastohost")
                        .accept(MediaType.APPLICATION_XML)
                        .content(inputParam))
                .andExpect(status().isBadRequest())
                .andExpect(content().xml(expResponse));
    }

    @Test
    public void nastohostTest3_3() throws Exception {

        String inputParam = "<MESSAGE>" +
                "<MSGID>603</MSGID>" +
                "<MSGTYPE></MSGTYPE>" +
                "<REPLYTO>183</REPLYTO>" +
                "<TIMESTAMP>20240516115447</TIMESTAMP>" +
                "<FACILITY>TEST</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>NAS</SENDER>" +
                "<RECIEVER>TEST</RECIEVER>" +
                "<SYSSTAT>" +
                "<ERROR_CODE>0</ERROR_CODE>" +
                "<DESCRIPTION>ok</DESCRIPTION>" +
                "</SYSSTAT>" +
                "</MESSAGE>";

        String expResponse = "<Sysstat>" +
                "<description>NasException; Required fields not filled in: MSGTYPE; Incorrect Message data; </description>" +
                "<error_code>400</error_code>" +
                "</Sysstat>";

        this.mvc.perform(post("/api/nastohost")
                        .accept(MediaType.APPLICATION_XML)
                        .content(inputParam))
                .andExpect(status().isBadRequest())
                .andExpect(content().xml(expResponse));
    }

    @Test
    public void nastohostTest4_4() throws Exception {

        String inputParam = "<MESSAGE>" +
                "<MSGID>603</MSGID>" +
                "<MSGTYPE>SYSSTAT</MSGTYPE>" +
                "<REPLYTO>183</REPLYTO>" +
                "<TIMESTAMP></TIMESTAMP>" +
                "<FACILITY>TEST</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>NAS</SENDER>" +
                "<RECIEVER>TEST</RECIEVER>" +
                "<SYSSTAT>" +
                "<ERROR_CODE>0</ERROR_CODE>" +
                "<DESCRIPTION>ok</DESCRIPTION>" +
                "</SYSSTAT>" +
                "</MESSAGE>";

        String expResponse = "<Sysstat>" +
                "<description>NasException; Required fields not filled in: TIMESTAMP; Incorrect TIMESTAMP; </description>" +
                "<error_code>400</error_code>" +
                "</Sysstat>";

        this.mvc.perform(post("/api/nastohost")
                        .accept(MediaType.APPLICATION_XML)
                        .content(inputParam))
                .andExpect(status().isBadRequest())
                .andExpect(content().xml(expResponse));
    }

    @Test
    public void nastohostTest5_5() throws Exception {

        String inputParam = "<MESSAGE>" +
                "<MSGID>603</MSGID>" +
                "<MSGTYPE>SYSSTAT</MSGTYPE>" +
                "<REPLYTO>183</REPLYTO>" +
                "<TIMESTAMP>2024-05-16 11:54:47</TIMESTAMP>" +
                "<FACILITY>TEST</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>NAS</SENDER>" +
                "<RECIEVER>TEST</RECIEVER>" +
                "<SYSSTAT>" +
                "<ERROR_CODE>0</ERROR_CODE>" +
                "<DESCRIPTION>ok</DESCRIPTION>" +
                "</SYSSTAT>" +
                "</MESSAGE>";

        String expResponse = "<Sysstat>" +
                "<description>NasException; Incorrect TIMESTAMP; </description>" +
                "<error_code>400</error_code>" +
                "</Sysstat>";

        this.mvc.perform(post("/api/nastohost")
                        .accept(MediaType.APPLICATION_XML)
                        .content(inputParam))
                .andExpect(status().isBadRequest())
                .andExpect(content().xml(expResponse));
    }

    @Test
    public void nastohostTest6_6() throws Exception {

        String inputParam = "<MESSAGE>" +
                "<MSGID>603</MSGID>" +
                "<MSGTYPE>SYSSTAT</MSGTYPE>" +
                "<REPLYTO>183</REPLYTO>" +
                "<TIMESTAMP>20240516115447</TIMESTAMP>" +
                "<FACILITY>TEST</FACILITY>" +
                "<ACTION>SET1</ACTION>" +
                "<SENDER>NAS</SENDER>" +
                "<RECIEVER>TEST</RECIEVER>" +
                "<SYSSTAT>" +
                "<ERROR_CODE>0</ERROR_CODE>" +
                "<DESCRIPTION>ok</DESCRIPTION>" +
                "</SYSSTAT>" +
                "</MESSAGE>";

        String expResponse = "<Sysstat>" +
                "<description>NasException; Incorrect ACTION, must be SET or DELETE; </description>" +
                "<error_code>400</error_code>" +
                "</Sysstat>";

        this.mvc.perform(post("/api/nastohost")
                        .accept(MediaType.APPLICATION_XML)
                        .content(inputParam))
                .andExpect(status().isBadRequest())
                .andExpect(content().xml(expResponse));
    }

    @Test
    public void nastohostTest7_7() throws Exception {

        String inputParam = "<MESSAGE>" +
                "<MSGID>603</MSGID>" +
                "<MSGTYPE>SYSSTAT1</MSGTYPE>" +
                "<REPLYTO>183</REPLYTO>" +
                "<TIMESTAMP>20240516115447</TIMESTAMP>" +
                "<FACILITY>TEST</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>NAS</SENDER>" +
                "<RECIEVER>TEST</RECIEVER>" +
                "<SYSSTAT>" +
                "<ERROR_CODE>0</ERROR_CODE>" +
                "<DESCRIPTION>ok</DESCRIPTION>" +
                "</SYSSTAT>" +
                "</MESSAGE>";

        String expResponse = "<Sysstat>" +
                "<description>NasException; Incorrect Message data; </description>" +
                "<error_code>400</error_code>" +
                "</Sysstat>";

        this.mvc.perform(post("/api/nastohost")
                        .accept(MediaType.APPLICATION_XML)
                        .content(inputParam))
                .andExpect(status().isBadRequest())
                .andExpect(content().xml(expResponse));
    }

    @Test
    public void nastohostTest8_8() throws Exception {

        String inputParam = "<MESSAGE>" +
                "<MSGID>603</MSGID>" +
                "<MSGTYPE>SYSSTAT</MSGTYPE>" +
                "<REPLYTO>183</REPLYTO>" +
                "<TIMESTAMP>20240516115447</TIMESTAMP>" +
                "<FACILITY>TEST</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>NAS</SENDER>" +
                "<RECIEVER>TEST</RECIEVER>" +
                "<SYSSTAT1>" +
                "<ERROR_CODE>0</ERROR_CODE>" +
                "<DESCRIPTION>ok</DESCRIPTION>" +
                "</SYSSTAT1>" +
                "</MESSAGE>";

        String expResponse = "<Sysstat>" +
                "<description>NasException; Incorrect Message data; </description>" +
                "<error_code>400</error_code>" +
                "</Sysstat>";

        this.mvc.perform(post("/api/nastohost")
                        .accept(MediaType.APPLICATION_XML)
                        .content(inputParam))
                .andExpect(status().isBadRequest())
                .andExpect(content().xml(expResponse));
    }

    @Test
    public void hosttonasTest1_9() throws Exception {

        String reqBody = "<MESSAGE>" +
                "<MSGID>603</MSGID>" +
                "<MSGTYPE>SYSSTAT</MSGTYPE>" +
                "<REPLYTO></REPLYTO>" +
                "<TIMESTAMP>20240516115447</TIMESTAMP>" +
                "<FACILITY>TEST</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>S</SENDER>" +
                "<RECIEVER>TEST</RECIEVER>" +
                "<SYSSTAT>" +
                "<ERROR_CODE>0</ERROR_CODE>" +
                "<DESCRIPTION>ok</DESCRIPTION>" +
                "</SYSSTAT>" +
                "</MESSAGE>";

        given(this.nasService.callNas(reqBody)).willReturn(reqBody);

        this.mvc.perform(post("/api/hosttonas")
                        .accept(MediaType.APPLICATION_XML)
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
                "<FACILITY>TEST</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>S</SENDER>" +
                "<RECIEVER>TEST</RECIEVER>" +
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
                "<FACILITY>TEST</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>S</SENDER>" +
                "<RECIEVER>TEST</RECIEVER>" +
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
                "<FACILITY>TEST</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>S</SENDER>" +
                "<RECIEVER>TEST</RECIEVER>" +
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
                "<FACILITY>TEST</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>S</SENDER>" +
                "<RECIEVER>TEST</RECIEVER>" +
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
                "<FACILITY>TEST</FACILITY>" +
                "<ACTION>SET1</ACTION>" +
                "<SENDER>S</SENDER>" +
                "<RECIEVER>TEST</RECIEVER>" +
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
                "<FACILITY>TEST</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>S</SENDER>" +
                "<RECIEVER>TEST</RECIEVER>" +
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
                "<FACILITY>TEST</FACILITY>" +
                "<ACTION>SET</ACTION>" +
                "<SENDER>S</SENDER>" +
                "<RECIEVER>TEST</RECIEVER>" +
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
                        .content(reqBody))
                .andExpect(content().string(expResponse))
                .andExpect(status().isBadRequest());
    }

}
