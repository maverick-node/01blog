package com._blog.myblog.controller.Posts;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<List<Map<String, String>>> getAllPosts() {
        List<PostStruct> posts = repo.findAll();

        List<Map<String, String>> result = posts.stream().map(post -> Map.of(
                "id", post.getId().toString(),            // include ID to identify posts
                "title", post.getTitle().toString(),
                "text", post.getText(),
                "author", post.getAuthor() != null ? post.getAuthor() : "Unknown"
        )).toList();

        return ResponseEntity.ok(result);
    }
}
