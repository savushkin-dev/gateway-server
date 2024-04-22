package com.example.SpringBootBerezaServer.exceptions;

public class UserNotCreatedException extends RuntimeException {
    public UserNotCreatedException(String message){
        super(message);
    }
}
