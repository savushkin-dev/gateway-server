package com.host.SpringBootBerezaServer.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "BD_H2N")
public class Host2Nas extends MsgNasHost {
    public Host2Nas(int F_ID, String MSGID, String MSGTYPE, String REPLYTO, LocalDateTime TIMESTAMP, String FACILITY, String ACTION, String SENDER, String RECEIVER, String DATA, Long SID, String STATUS, String TEST, int ERRCODE, String ERRTEXT, LocalDateTime DT, String USERID) {
        super(F_ID, MSGID, MSGTYPE, REPLYTO, TIMESTAMP, FACILITY, ACTION, SENDER, RECEIVER, DATA, SID, STATUS, TEST, ERRCODE, ERRTEXT, DT, USERID);
    }

    public Host2Nas() {
    }

    public Host2Nas(String MSGID, String MSGTYPE, String REPLYTO, LocalDateTime TIMESTAMP, String FACILITY, String ACTION, String SENDER, String RECEIVER) {
        super(MSGID, MSGTYPE, REPLYTO, TIMESTAMP, FACILITY, ACTION, SENDER, RECEIVER);
    }
}
