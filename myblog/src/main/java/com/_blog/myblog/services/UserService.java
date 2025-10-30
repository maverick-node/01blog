package com._blog.myblog.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com._blog.myblog.model.CommentStruct;
import com._blog.myblog.model.PostStruct;
import com._blog.myblog.model.UserStruct;
import com._blog.myblog.repository.CommentRepository;
import com._blog.myblog.repository.PostRepository;
import com._blog.myblog.repository.UserRepository;

@Service
public class UserService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, PostRepository postRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public UserStruct registerUser(String email, String rawPassword, String username, String bio, int age) {
        UserStruct user = new UserStruct();
        user.setMail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setUsername(username); 
        user.setBio(bio);
        user.setAge(age);
        return userRepository.save(user);
    }
    public PostStruct savePost(String author, String title,String text){
        PostStruct post =new PostStruct();
        post.setAuthor(author);
        post.setText(text);
        post.setTitle(title);
        return postRepository.save(post);
    }

    public CommentStruct saveComment(String username, String comment, int post_id){
        CommentStruct com =new CommentStruct();
        com.setUsername(username);
        com.setComment(comment);
        com.setPostId(post_id);
        return commentRepository.save(com);
    }
}
