package com.devmam.zalomini.controller;

import com.devmam.zalomini.dto.request.UserLoginDTO;
import com.devmam.zalomini.dto.request.UserRegisterDTO;
import com.devmam.zalomini.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader("Authorization") String authHeader) {
        return userService.getProfile(authHeader);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO dto) {
        return userService.login(dto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO dto) {
        return userService.register(dto);
    }
}
