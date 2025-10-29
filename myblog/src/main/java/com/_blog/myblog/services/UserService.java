package com._blog.myblog.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com._blog.myblog.model.PostStruct;
import com._blog.myblog.model.UserStruct;
import com._blog.myblog.repository.PostRepository;
import com._blog.myblog.repository.UserRepository;

@Service
public class UserService {

    private final PostRepository postRepository;
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.postRepository = postRepository;
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
}
