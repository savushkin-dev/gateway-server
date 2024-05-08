package com.host.SpringBootBerezaServer.exceptions.NAS;

public class NasRemoteServerException extends RuntimeException {

    private String response;

    public NasRemoteServerException(String message){
        super(message);
    }

    public NasRemoteServerException(String message, String response){
        super(message);
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
