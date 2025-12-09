package com.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "likes")
public class LikesStruct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private PostsStruct post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserStruct user;

    @Column(nullable = false)
    private Boolean liked;
}
