package com.host.SpringBootBerezaServer.exceptions;

public class UserNotCreatedException extends RuntimeException {
    public UserNotCreatedException(String message){
        super(message);
    }
}
