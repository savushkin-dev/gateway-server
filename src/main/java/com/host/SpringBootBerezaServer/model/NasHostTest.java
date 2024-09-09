package com.host.SpringBootBerezaServer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "BD_NHTEST")
public class NasHostTest extends MsgNasHost {

}
