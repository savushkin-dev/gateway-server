package com.example.SpringBootBerezaServer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BD_EDOC")
public class Edoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "F_GUID")
    private long FGUID;

    @Column(name = "User_id")
    private int USERID;

    @Column(name = "F_ID",insertable = false, updatable = false)
    private long FID;

    @Column(name = "F_TM")
    private LocalDateTime FTM;

    @Column(name = "F_DEL")
    private int FDEL;

    @Column(name = "EDI")
    private String EDI;

    @Column(name = "TP")
    private String TP;

    @Column(name = "PST")
    private String PST;

    @Column(name = "NDE")
    private String NDE;

    @DateTimeFormat(pattern = "yyyyMMdd[ [HH][:mm][:ss][.SSS]]")  //разобраться с преобразованием из БД
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT")
    private LocalDateTime DT;

    @Column(name = "DTDOC")
    private LocalDateTime DTDOC;

    @Column(name = "RECEIVER")
    private long RECEIVER;

    @Column(name = "SENDER")
    private long SENDER;

    @Column(name = "DOC")
    private String DOC;

    @Column(name = "ID")
    private long ID;

    @Column(name = "DTINS")
    private LocalDateTime DTINS;

    @Column(name = "DTUPD")
    private LocalDateTime DTUPD;

    public Edoc(Edoc edoc) {
//        this.FGUID = pricat.getFGUID();
//        this.USERID = pricat.getUSERID();
        this.FID = edoc.getFID();
        this.FTM = edoc.getFTM();
        this.FDEL = edoc.getFDEL();
        this.EDI = edoc.getEDI();
        this.TP = edoc.getTP();
        this.PST = edoc.getPST();
        this.NDE = edoc.getNDE();
        this.DT = edoc.getDT();
        this.DTDOC = edoc.getDTDOC();
        this.RECEIVER = edoc.getRECEIVER();
        this.SENDER = edoc.getSENDER();
        this.DOC = edoc.getDOC();
        this.ID = edoc.getID();
        this.DTINS = edoc.getDTINS();
        this.DTUPD = edoc.getDTUPD();
    }

    @Override
    public String toString() {
        return "Pricat{" +
                "F_GUID=" + FGUID +
                ", User_id=" + USERID +
                ", F_ID=" + FID +
                ", F_TM=" + FTM +
                ", F_DEL=" + FDEL +
                ", EDI='" + EDI + '\'' +
                ", TP='" + TP + '\'' +
                ", PST='" + PST + '\'' +
                ", NDE='" + NDE + '\'' +
                ", DT=" + DT +
                ", DTDOC=" + DTDOC +
                ", RECEIVER=" + RECEIVER +
                ", SENDER=" + SENDER +
//                ", DOC='" + DOC + '\'' +
                ", ID=" + ID +
                ", DTINS=" + DTINS +
                ", DTUPD=" + DTUPD +
                '}';
    }
}
