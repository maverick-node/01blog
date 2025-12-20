package com.controller.Middleware;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Exceptions.UnauthorizedActionException;

import com.Model.UserStruct;

import com.dto.UserDTOMiddle;

import com.services.UserServiceMiddle;
@RestController
public class MiddlewareController {

    private final UserServiceMiddle userService;

    public MiddlewareController(UserServiceMiddle userService) {
        this.userService = userService;
    }

    @GetMapping("/middleware")
    public ResponseEntity<UserDTOMiddle> getUser(@CookieValue(value = "jwt", required = false)String jwt) {
        UserStruct user = userService.getUserFromJwt(jwt);
        if (user == null || jwt.isEmpty()){
            throw new UnauthorizedActionException("You are not logged");
        }
        UserDTOMiddle response = new UserDTOMiddle(
            user.getUsername(),
            user.getMail(),
            user.getBio(),
            user.getAge(),
            user.getRole(),
            user.isBanned()
        );

        return ResponseEntity.ok(response);
    }
}
