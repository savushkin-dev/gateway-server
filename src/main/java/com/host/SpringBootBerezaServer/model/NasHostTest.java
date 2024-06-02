package com.host.SpringBootBerezaServer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "BD_NHTEST")
public class NasHostTest extends MsgNasHost {
    public NasHostTest(int F_ID, String MSGID, String MSGTYPE, String REPLYTO, LocalDateTime TIMESTAMP, String FACILITY, String ACTION, String SENDER, String RECEIVER, String DATA, Long SID, String STATUS, String TEST, int ERRCODE, String ERRTEXT, LocalDateTime DT, String USERID) {
        super(F_ID, MSGID, MSGTYPE, REPLYTO, TIMESTAMP, FACILITY, ACTION, SENDER, RECEIVER, DATA, SID, STATUS, TEST, ERRCODE, ERRTEXT, DT, USERID);
    }

    public NasHostTest() {
    }

    public NasHostTest(String MSGID, String MSGTYPE, String REPLYTO, LocalDateTime TIMESTAMP, String FACILITY, String ACTION, String SENDER, String RECEIVER) {
        super(MSGID, MSGTYPE, REPLYTO, TIMESTAMP, FACILITY, ACTION, SENDER, RECEIVER);
    }
}
