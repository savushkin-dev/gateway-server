package com.example.SpringBootGraalVMPythonGateway.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AppError {

    private String message;

    private Date timestamp;

    public AppError(String message) {
        this.message = message;
        this.timestamp = new Date();
    }

}
