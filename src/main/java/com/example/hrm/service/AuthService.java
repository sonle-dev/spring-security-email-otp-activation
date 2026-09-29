package com.example.hrm.service;

import com.example.hrm.dto.ActiveUserRequest;
import com.example.hrm.dto.AuthResponse;
import com.example.hrm.dto.LoginRequest;
import com.example.hrm.dto.RegisterRequest;
import com.example.hrm.entity.User;

public interface AuthService {
    User register(RegisterRequest request);
    String activeUser(ActiveUserRequest request);
    AuthResponse login(LoginRequest request);
}