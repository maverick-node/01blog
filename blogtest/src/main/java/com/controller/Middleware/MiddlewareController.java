package com.controller.Middleware;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Model.UserStruct;
import com.Repository.UserRepo;
import com.dto.UserDTOMiddle;
import com.services.JwtService;
import com.services.UserServiceMiddle;
@RestController
public class MiddlewareController {

    private final UserServiceMiddle userService;

    public MiddlewareController(UserServiceMiddle userService) {
        this.userService = userService;
    }

    @GetMapping("/middleware")
    public ResponseEntity<UserDTOMiddle> getUser(@CookieValue("jwt") String jwt) {
        UserStruct user = userService.getUserFromJwt(jwt);

        UserDTOMiddle response = new UserDTOMiddle(
            user.getUsername(),
            user.getMail(),
            user.getBio(),
            user.getAge(),
            user.getRole()
        );

        return ResponseEntity.ok(response);
    }
}
