package com.Model;

import javax.management.Notification;

import jakarta.persistence.*;

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
    private boolean isBanned = false;

    @Column(nullable = false)
    private String role = "USER";

    // Link to posts
    @OneToMany(mappedBy = "authorUser", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @lombok.ToString.Exclude
    private java.util.List<PostsStruct> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @lombok.ToString.Exclude
    private java.util.List<LikesStruct> likes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @lombok.ToString.Exclude
    private java.util.List<NotificationStruct> notifications;

    // Link to comments
    @OneToMany(mappedBy = "authorUser", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @lombok.ToString.Exclude
    private java.util.List<CommentStruct> comments;

    @OneToMany(mappedBy = "reporter", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @lombok.ToString.Exclude
    private java.util.List<ReportStruct> reportsMade;




}
