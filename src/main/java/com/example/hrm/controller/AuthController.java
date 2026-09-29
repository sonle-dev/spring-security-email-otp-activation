package com.example.hrm.controller;

import com.example.hrm.dto.ActiveUserRequest;
import com.example.hrm.dto.AuthResponse;
import com.example.hrm.dto.LoginRequest;
import com.example.hrm.dto.RegisterRequest;
import com.example.hrm.entity.User;
import com.example.hrm.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/active-user")
    public ResponseEntity<String> activeUser(@RequestBody ActiveUserRequest request) {
        return ResponseEntity.ok(authService.activeUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}