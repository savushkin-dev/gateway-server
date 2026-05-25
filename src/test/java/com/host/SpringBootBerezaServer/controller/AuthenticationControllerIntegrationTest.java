package com.host.SpringBootBerezaServer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.host.SpringBootBerezaServer.SpringBootBerezaServerApplication;
import com.host.SpringBootBerezaServer.dto.LoginRequestDTO;
import com.host.SpringBootBerezaServer.model.User;
import com.host.SpringBootBerezaServer.repositories.UsersRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = SpringBootBerezaServerApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper mapper;
    private String testUsername;
    private String testPassword;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();

        // Генерируем уникальные данные для каждого теста
        testUsername = "test_" + UUID.randomUUID().toString().substring(0, 8);
        testPassword = "Pass_" + UUID.randomUUID().toString().substring(0, 8) + "!";

        User user = new User();
        user.setUsername(testUsername);
        user.setPassword(passwordEncoder.encode(testPassword));

        usersRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        usersRepository.deleteUserByUsername(testUsername);
    }

    @Test
    void performAuthenticationTest1_17() throws Exception {
        LoginRequestDTO requestDTO = new LoginRequestDTO(testUsername, testPassword);

        mvc.perform(post("/api/authentication/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andExpect(status().isOk());
    }

    @Test
    void performAuthenticationTest2_18() throws Exception {
        LoginRequestDTO requestDTO = new LoginRequestDTO(testUsername, "wrong_password");

        mvc.perform(post("/api/authentication/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(jsonPath("$.message").value("BadCredentialsException; "))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(status().is4xxClientError());
    }
}