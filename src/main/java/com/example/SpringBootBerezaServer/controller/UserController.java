package com.example.SpringBootBerezaServer.controller;

import com.example.SpringBootBerezaServer.service.UserService;
import com.example.SpringBootBerezaServer.util.UserOrgValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final UserOrgValidator userOrgValidator;

    @Autowired
    public UserController(UserService userService, UserOrgValidator userOrgValidator) {
        this.userService = userService;
        this.userOrgValidator = userOrgValidator;
    }






}
