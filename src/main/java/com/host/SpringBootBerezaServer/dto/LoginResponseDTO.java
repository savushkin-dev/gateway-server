package com.host.SpringBootBerezaServer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {

    private String uuid;

    public LoginResponseDTO(String uuid) {
        this.uuid = uuid;
    }
}
