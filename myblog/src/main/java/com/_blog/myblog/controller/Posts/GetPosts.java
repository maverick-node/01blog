package com._blog.myblog.controller.Posts;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog.myblog.model.PostStruct;
import com._blog.myblog.repository.PostRepository;

@RestController
public class GetPosts {

    private final PostRepository repo;

    public GetPosts(PostRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/get-posts")
    public ResponseEntity<List<Map<String, Object>>> getAllPosts() {
        List<PostStruct> posts = repo.findAll();

        List<Map<String, Object>> result = posts.stream()
            .sorted((post1, post2) -> post2.getId().compareTo(post1.getId()))
            .map(post -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", post.getId());
                m.put("title", post.getTitle());
                m.put("text", post.getText());
                m.put("author", post.getAuthor() != null ? post.getAuthor() : "Unknown");
                if (post.getMediaUrl() != null) m.put("mediaUrl", post.getMediaUrl());
                if (post.getMediaType() != null) m.put("mediaType", post.getMediaType());
                return m;
            }).toList();

        return ResponseEntity.ok(result);
    }
}
