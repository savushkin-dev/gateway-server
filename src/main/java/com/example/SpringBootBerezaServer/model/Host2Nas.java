package com.example.SpringBootBerezaServer.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "BD_H2N")
public class Host2Nas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "F_ID")
    private int F_ID;

    @Column(name = "MSGID")
    private String MSGID;

    @Column(name = "MSGTYPE")
    private String MSGTYPE;

    @Column(name = "REPLYTO")
    private String REPLYTO;

    @Column(name = "TIMESTAMP")
    private LocalDateTime TIMESTAMP;

    @Column(name = "FACILITY")
    private String FACILITY;

    @Column(name = "ACTION")
    private String ACTION;

    @Column(name = "SENDER")
    private String SENDER;

    @Column(name = "RECEIVER")
    private String RECEIVER;

    @Column(name = "DATA")
    private String DATA;

    @Column(name = "S_ID")
    private Long SID;

    @Column(name = "STATUS")
    private String STATUS;

    @Column(name = "TEST")
    private String TEST;

    @Column(name = "ERRCODE")
    private int ERRCODE;

    @Column(name = "ERRTEXT")
    private String ERRTEXT;

    @Column(name = "DT")
    private LocalDateTime DT;

    @Column(name = "USERID")
    private String USERID;

    public Host2Nas(String MSGID, String MSGTYPE, String REPLYTO, LocalDateTime TIMESTAMP, String FACILITY, String ACTION, String SENDER, String RECEIVER) {
        this.MSGID = MSGID;
        this.MSGTYPE = MSGTYPE;
        this.REPLYTO = REPLYTO;
        this.TIMESTAMP = TIMESTAMP;
        this.FACILITY = FACILITY;
        this.ACTION = ACTION;
        this.SENDER = SENDER;
        this.RECEIVER = RECEIVER;
    }
}
