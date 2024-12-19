package com.host.SpringBootBerezaServer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "BD_H2N")
public class Host2Nas extends MsgNasHost {

    public static Host2Nas convertFromMsgNasHost(MsgNasHost msgNasHost){
        Host2Nas obj = new Host2Nas();

        obj.setF_ID(msgNasHost.getF_ID());
        obj.setMSGID(msgNasHost.getMSGID());
        obj.setMSGTYPE(msgNasHost.getMSGTYPE());
        obj.setREPLYTO(msgNasHost.getREPLYTO());
        obj.setTIMESTAMP(msgNasHost.getTIMESTAMP());
        obj.setFACILITY(msgNasHost.getFACILITY());
        obj.setACTION(msgNasHost.getACTION());
        obj.setSENDER(msgNasHost.getSENDER());
        obj.setRECEIVER(msgNasHost.getRECEIVER());
        obj.setDATA(msgNasHost.getDATA());
        obj.setSID(msgNasHost.getSID());
        obj.setSTATUS(msgNasHost.getSTATUS());
        obj.setTEST(msgNasHost.getTEST());
        obj.setERRCODE(msgNasHost.getERRCODE());
        obj.setERRTEXT(msgNasHost.getERRTEXT());
        obj.setDT(msgNasHost.getDT());
        obj.setUSERID(msgNasHost.getUSERID());

        return obj;
    }

}
