package com.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Data
@Table(name = "comments")
public class CommentStruct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;
    @Column(nullable = false)
    private String username;
    @Column(name = "post_id", nullable = false)
    private int postId;

    private LocalDateTime createdAt = LocalDateTime.now();
}
