package com.controller.Comments;

import java.util.Map;

import javax.xml.stream.events.Comment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.Model.CommentStruct;
import com.Repository.CommentRepo;
import com.services.JwtService;

@RestController
public class GetComments {
        private final CommentRepo commentRepo;
        private final JwtService jwtService;

        public GetComments(CommentRepo commentRepo, JwtService jwtService) {
                this.commentRepo = commentRepo;
                this.jwtService = jwtService;
        }
        @GetMapping("/posts/{id}/comments")
        public ResponseEntity<Map<String, Object>> getComments(@CookieValue("jwt") String jwt, @PathVariable("id") int postId) {
                System.out.println("Get comments request for post ID: " + postId);
                String username = jwtService.extractUsername(jwt);
                if (username == null || username.isEmpty()) {
                        return ResponseEntity
                                .badRequest()
                                .body(Map.of("message", "Error: Invalid JWT token Login please!"));
                }
         
                return ResponseEntity.ok(Map.of("comments", commentRepo.findAllByPostId(postId)));
        }
}
