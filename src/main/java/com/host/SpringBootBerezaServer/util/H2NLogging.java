package com.host.SpringBootBerezaServer.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class H2NLogging {

    public static void writeLogH2N(String request, String response, long durParsing, long durDB, long durKafka) {
        log.info("\nDuration parsing+validation: " + durParsing + "\n"
                + "Duration save in DB: " + durDB + "\n"
                + "Duration send to kafka: " + durKafka + "\n\n"
                + "Request:" + "\n"
                + request + "\n\n"
                + "Response:" + "\n"
                + response + "\n");
    }

    public static void writeLogH2N(String request, String response, String error, long durParsing, long durDB, long durKafka) {
        log.error("\nDuration parsing+validation: " + durParsing + "\n"
                + "Duration save in DB: " + durDB + "\n"
                + "Duration send to kafka: " + durKafka + "\n\n"
                + "Request:" + "\n"
                + request + "\n\n"
                + "Response:" + "\n"
                + response + "\n\n"
                + "Error:" + "\n"
                + error + "\n");
    }
}
