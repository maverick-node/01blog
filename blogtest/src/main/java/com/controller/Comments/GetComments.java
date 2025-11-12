package com.controller.Comments;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
        @GetMapping("/get-comments")
        public ResponseEntity<Map<String, String>> getComments(@CookieValue("jwt") String jwt) {
                String username = jwtService.extractUsername(jwt);
                if (username == null || username.isEmpty()) {
                        return ResponseEntity
                                .badRequest()
                                .body(Map.of("message", "Error: Invalid JWT token Login please!"));
                }
                return ResponseEntity.ok(Map.of("comments", commentRepo.findAll().toString()));
        }
}
