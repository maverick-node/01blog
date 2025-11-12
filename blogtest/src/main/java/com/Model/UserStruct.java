package com.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.persistence.Column;
import lombok.Data;


@Data

@Entity
@Table(name = "users")
public class UserStruct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String mail;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String bio;
    @Column(nullable = false)
    private int age;
    
    @Column(nullable = false)
    private String role = "USER";

}
