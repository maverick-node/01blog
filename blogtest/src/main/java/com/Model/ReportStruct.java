package com.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "reports")
public class ReportStruct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // The user who reported
    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private UserStruct reporter;

    // The user being reported
    @ManyToOne
    @JoinColumn(name = "target_user_id", nullable = false)
        @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)

    private UserStruct targetUser;

    // The post being reported
    @ManyToOne
    @JoinColumn(name = "reported_post_id")
        @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)

    private PostsStruct reportedPost;

    private String reason;
    private boolean resolved = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
