package com.example.SpringBootBerezaServer;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;


@EnableKafka
@SpringBootApplication
public class SpringBootBerezaServerApplication {


	public static void main(String[] args) {
		SpringApplication.run(SpringBootBerezaServerApplication.class, args);
	}

}
