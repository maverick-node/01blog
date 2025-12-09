package com.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "files")
public class FileStruct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fileName; // e.g. "photo.jpg"
    private String filePath; // e.g. "/uploads/abc123.jpg"
    private String fileType; // e.g. "image/jpeg"
    private Long fileSize;

    // One File belongs to One Post
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostsStruct post;
}