package com.example.SpringBootBerezaServer.exceptions;

public class UserNotUpdatedException extends RuntimeException {
    public UserNotUpdatedException(String message){
        super(message);
    }
}
