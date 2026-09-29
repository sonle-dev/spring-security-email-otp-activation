package com.example.hrm.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otpCode);
}