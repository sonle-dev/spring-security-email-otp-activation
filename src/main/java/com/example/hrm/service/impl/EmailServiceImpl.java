package com.example.hrm.service.impl;

import com.example.hrm.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String toEmail, String otpCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Mã xác thực tài khoản HRM (OTP)");
            message.setText("Chào bạn,\n\nMã OTP kích hoạt tài khoản của bạn là: " + otpCode
                    + "\nMã có hiệu lực trong vòng 5 phút.\nVui lòng không chia sẻ mã này cho ai.");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Gửi mail thất bại (in log console): Mã OTP cho email " + toEmail + " là: " + otpCode);
        }
    }
}