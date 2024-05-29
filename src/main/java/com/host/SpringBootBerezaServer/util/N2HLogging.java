package com.host.SpringBootBerezaServer.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class N2HLogging {

    public static void writeLogN2H(String request, long durParsing, long durDB, long durKafka) {
        log.info("\nDuration parsing+validation: " + durParsing + "\n"
                + "Duration save in DB: " + durDB + "\n"
                + "Duration send to kafka: " + durKafka + "\n\n"
                + "Request:" + "\n"
                + request + "\n");

    }

    public static void writeLogN2H(String request, String error, long durParsing, long durDB, long durKafka) {
        log.error("\nDuration parsing+validation: " + durParsing + "\n"
                + "Duration save in DB: " + durDB + "\n"
                + "Duration send to kafka: " + durKafka + "\n\n"
                + "Request:" + "\n"
                + request + "\n\n"
                + "Error:" + "\n"
                + error + "\n");

    }
}
