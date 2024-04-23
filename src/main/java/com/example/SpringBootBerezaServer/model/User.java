package com.example.SpringBootBerezaServer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "BD_NASUSR")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "F_ID")
    private int F_ID;


    @Column(name = "USERNAME")
    private String username;


    @Column(name = "PASSWORD")
    private String password;


    public User() {

    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "F_ID=" + F_ID +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
