package com.example.SpringBootGraalVMPythonGateway.exceptions;

public class UserNotUpdatedException extends RuntimeException {
    public UserNotUpdatedException(String message){
        super(message);
    }
}
