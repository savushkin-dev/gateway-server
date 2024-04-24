package com.example.SpringBootBerezaServer.controller;

import com.example.SpringBootBerezaServer.service.Host2NasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/test")
public class TestController {



    @Autowired
    public TestController(Host2NasService host2NasService) {
        this.host2NasService = host2NasService;
    }

    @GetMapping()
    public ResponseEntity<?> test() {


        return ResponseEntity.ok("test!");
    }


}
