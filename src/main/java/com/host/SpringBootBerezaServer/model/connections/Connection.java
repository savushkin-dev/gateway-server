package com.host.SpringBootBerezaServer.model.connections;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Connection {

    private String type;
    private String topic;
    private String url;
    private String bearer;

}
