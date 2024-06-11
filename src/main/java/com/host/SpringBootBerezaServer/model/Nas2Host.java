package com.host.SpringBootBerezaServer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "BD_N2H")
public class Nas2Host extends MsgNasHost {

}
