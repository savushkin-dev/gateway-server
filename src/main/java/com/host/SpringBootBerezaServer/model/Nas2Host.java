package com.host.SpringBootBerezaServer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "BD_N2H")
public class Nas2Host extends MsgNasHost {

    public Nas2Host() {
    }

    public Nas2Host(String MSGID, String MSGTYPE, String REPLYTO, LocalDateTime TIMESTAMP, String FACILITY, String ACTION, String SENDER, String RECEIVER) {
        super(MSGID, MSGTYPE, REPLYTO, TIMESTAMP, FACILITY, ACTION, SENDER, RECEIVER);
    }

    public Nas2Host(int f_ID, String MSGID, String MSGTYPE, String REPLYTO, LocalDateTime TIMESTAMP, String FACILITY, String ACTION, String SENDER, String RECEIVER, String DATA, Long SID, String STATUS, String TEST, int ERRCODE, String ERRTEXT, LocalDateTime DT, String USERID) {
        super(f_ID, MSGID, MSGTYPE, REPLYTO, TIMESTAMP, FACILITY, ACTION, SENDER, RECEIVER, DATA, SID, STATUS, TEST, ERRCODE, ERRTEXT, DT, USERID);
    }
}
