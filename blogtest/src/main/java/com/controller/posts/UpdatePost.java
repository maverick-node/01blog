package com.controller.posts;

import com.Model.PostsStruct;
import com.services.PostService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class UpdatePost {

    private final PostService postService;

    public UpdatePost(PostService postService) {
        this.postService = postService;
    }

    @PutMapping(value = "/update-post/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public ResponseEntity<?> updatePost(
            @PathVariable Integer postId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile[] files,
            @RequestParam(value = "removeMediaIds", required = false) List<Integer> removeMediaIds,
            @CookieValue("jwt") String jwt) {

        PostsStruct updated = postService.updatePostWithMultipleMedia(
                postId, title, content, files, removeMediaIds, jwt);

        return ResponseEntity.ok(Map.of(
                "message", "Post updated successfully",
                "mediaPaths", updated.getMediaPaths(),
                "mediaTypes", updated.getMediaTypes(),
                "mediaIds", updated.getMediaIds()));
    }
}
