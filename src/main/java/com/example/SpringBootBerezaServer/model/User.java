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
@Table(name = "User_org")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotEmpty(message = "Must not be empty")
    @Size(min = 3, max = 100, message = "Must be between 4 and 100 characters long")
    @Column(name = "username")
    private String username;

    @Email
    @NotEmpty(message = "Must not be empty")
    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "gln")
    private long gln;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "profile_update")
    private LocalDateTime profileUpdate;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;


    public User() {

    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String username, String email, String password, long gln) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.gln = gln;
    }

    public User(String username, String email, String password, long gln, String lastName, String firstName, String middleName, String phone, LocalDateTime profileUpdate, LocalDateTime lastLogin) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.gln = gln;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.phone = phone;
        this.profileUpdate = profileUpdate;
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return "UserOrg{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", gln=" + gln +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", phone='" + phone + '\'' +
                ", profileUpdate=" + profileUpdate +
                ", lastLogin=" + lastLogin +
                '}';
    }
}
