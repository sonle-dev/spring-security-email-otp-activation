package com.example.hrm.service.impl;

import com.example.hrm.dto.ActiveUserRequest;
import com.example.hrm.dto.AuthResponse;
import com.example.hrm.dto.LoginRequest;
import com.example.hrm.dto.RegisterRequest;
import com.example.hrm.entity.User;
import com.example.hrm.repository.UserRepository;
import com.example.hrm.security.JwtTokenProvider;
import com.example.hrm.service.AuthService;
import com.example.hrm.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final EmailService emailService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenProvider tokenProvider,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.emailService = emailService;
    }

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên đăng nhập đã tồn tại: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email đã được sử dụng: " + request.getEmail());
        }

        // Tạo OTP 6 chữ số ngẫu nhiên
        String otp = String.format("%06d", new Random().nextInt(999999));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role("USER")
                .enabled(false) // Mặc định chưa kích hoạt
                .otpCode(otp)
                .otpExpiration(LocalDateTime.now().plusMinutes(5)) // Hiệu lực 5 phút
                .build();

        User savedUser = userRepository.save(user);

        // Gửi email chứa OTP
        emailService.sendOtpEmail(savedUser.getEmail(), otp);

        return savedUser;
    }

    @Override
    public String activeUser(ActiveUserRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không tồn tại!"));

        if (user.isEnabled()) {
            return "Tài khoản đã được kích hoạt trước đó.";
        }

        if (user.getOtpCode() == null || !user.getOtpCode().equals(request.getOtp())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã OTP không chính xác!");
        }

        if (user.getOtpExpiration().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã OTP đã hết hạn!");
        }

        user.setEnabled(true);
        user.setOtpCode(null);
        user.setOtpExpiration(null);
        userRepository.save(user);

        return "Kích hoạt tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.";
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String token = tokenProvider.generateToken(authentication);

            return AuthResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .username(authentication.getName())
                    .message("Đăng nhập thành công")
                    .build();
        } catch (DisabledException ex) {
            // Khi UserPrincipal.isEnabled() trả về false
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng active tài khoản trước khi đăng nhập !");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password incorrect");
        }
    }
}