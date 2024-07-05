package com.host.SpringBootBerezaServer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Sysstat {

    @JsonProperty(index=2)
    private int Error_code;

    @JsonProperty(index=1)
    private String Description;

}
