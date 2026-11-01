package com.yourhotel.booking.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.yourhotel.booking.dto.request.AdminRegisterRequest; // 🚀 নতুন ইমপোর্ট
import com.yourhotel.booking.dto.request.LoginRequest;
import com.yourhotel.booking.dto.request.RegisterRequest;
import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.dto.response.AuthResponse;
import com.yourhotel.booking.dto.response.UserResponse;
import com.yourhotel.booking.entity.User;
import com.yourhotel.booking.repository.UserRepository;
import com.yourhotel.booking.security.JwtService;
import com.yourhotel.booking.service.AuthService;
import com.yourhotel.booking.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private static final String GOOGLE_CLIENT_ID = "713645495799-h4vja6vn4nhpdnr2u2cjcdo3ljtd6svn.apps.googleusercontent.com";

    public AuthController(AuthService authService, UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder, JwtService jwtService, UserDetailsService userDetailsService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Registration successful", authService.register(request)));
    }

    // 🚀 ADMIN REGISTRATION ENDPOINT (এটি যোগ করা হলো)
    @PostMapping("/register-admin")
    public ResponseEntity<ApiResponse<AuthResponse>> registerAdmin(@Valid @RequestBody AdminRegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Admin registered successfully", authService.registerAdmin(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.login(request)));
    }

    // 🚀 Google Login
    @PostMapping("/google-login")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                User user = userRepository.findByEmail(email).orElseGet(() -> {
                    User newUser = User.builder()
                            .name(name)
                            .email(email)
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .role(User.Role.USER)
                            .enabled(true)
                            .build();
                    return userRepository.save(newUser);
                });

                UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                String accessToken = jwtService.generateAccessToken(userDetails);
                String refreshToken = jwtService.generateRefreshToken(userDetails);

                AuthResponse response = AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .tokenType("Bearer")
                        .user(UserResponse.fromEntity(user))
                        .build();
                return ResponseEntity.ok(ApiResponse.success("Google login successful", response));
            }
        } catch (Exception e) {
            throw new RuntimeException("Google login failed: " + e.getMessage());
        }
        throw new RuntimeException("Invalid Google token");
    }

    // 🚀 Forgot Password (Send PIN)
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with this email"));
        String pin = String.format("%06d", new Random().nextInt(999999));
        user.setResetPin(pin);
        user.setResetPinExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        emailService.sendEmail(email, "Password Reset PIN - HotelReserve", "Your Password Reset PIN is: " + pin + "\nThis PIN will expire in 15 minutes.");
        return ResponseEntity.ok(ApiResponse.success("PIN sent to your email", null));
    }

    // 🚀 Reset Password with PIN
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String pin = body.get("pin");
        String newPassword = body.get("newPassword");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getResetPin() == null || !user.getResetPin().equals(pin)) throw new RuntimeException("Invalid PIN");
        if (user.getResetPinExpiry().isBefore(LocalDateTime.now())) throw new RuntimeException("PIN has expired");
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPin(null);
        user.setResetPinExpiry(null);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("Password reset successful", null));
    }
}
