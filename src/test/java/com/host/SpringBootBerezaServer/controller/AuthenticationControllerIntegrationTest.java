package com.host.SpringBootBerezaServer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.host.SpringBootBerezaServer.SpringBootBerezaServerApplication;
import com.host.SpringBootBerezaServer.dto.LoginRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = SpringBootBerezaServerApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(
        locations = "classpath:application-test.properties")
class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    private ObjectMapper mapper;

    @BeforeEach
    public void before(){
        mapper = new ObjectMapper();
    }

    @Test
    public void performAuthenticationTest1_17() throws Exception {

        LoginRequestDTO requestDTO = new LoginRequestDTO("testAccount","test2349$");

        this.mvc.perform(post("/api/authentication/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andExpect(status().isOk());
    }

    @Test
    public void performAuthenticationTest2_18() throws Exception {

        LoginRequestDTO requestDTO = new LoginRequestDTO("testAccount","test32");

        this.mvc.perform(post("/api/authentication/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(jsonPath("$.message").value("BadCredentialsException; "))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(status().is4xxClientError());
    }




}
