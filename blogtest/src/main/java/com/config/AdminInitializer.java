package com.config;

import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.Model.UserStruct;
import com.Repository.UserRepo;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepo userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String adminEmail = "admin@admin.com";
        var find = userRepository.findByMail(adminEmail);
        var find2 = userRepository.findByUsername("admin");
        if (find == null && find2 == null) {
            UserStruct admin = new UserStruct();
            admin.setMail(adminEmail);
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole("ADMIN");
            admin.setUsername("admin");
            admin.setAge(30);
            admin.setBio("I am the admin user.");
            admin.setUserUuid(UUID.randomUUID().toString());

            userRepository.save(admin);

            System.out.println("✅ ADMIN user created");
        } else {
            System.out.println("ℹ️ ADMIN user already exists");
        }
    }
}
