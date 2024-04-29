package com.example.SpringBootBerezaServer.configuration.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {

    @Bean
    public NewTopic nasToHost() {
        return TopicBuilder.name("NasToHost").partitions(1).build();
    }

    @Bean
    public NewTopic hostToNas() {
        return TopicBuilder.name("HostToNas").partitions(1).build();
    }
}
