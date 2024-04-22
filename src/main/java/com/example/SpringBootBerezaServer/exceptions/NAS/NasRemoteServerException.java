package com.example.SpringBootBerezaServer.exceptions.NAS;

public class NasRemoteServerException extends RuntimeException {
    public NasRemoteServerException(String message){
        super(message);
    }
}
