package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.dto.response.UserResponse;
import com.yourhotel.booking.entity.Setting;
import com.yourhotel.booking.entity.User;
import com.yourhotel.booking.exception.BadRequestException;
import com.yourhotel.booking.repository.SettingRepository;
import com.yourhotel.booking.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSettingsController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SettingRepository settingRepository;

    public AdminSettingsController(UserRepository userRepository, PasswordEncoder passwordEncoder, SettingRepository settingRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.settingRepository = settingRepository;
    }

    // ============ PROFILE & PASSWORD ============
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new BadRequestException("User not found"));
        return ResponseEntity.ok(ApiResponse.success(UserResponse.fromEntity(user)));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestBody Map<String, String> payload, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new BadRequestException("User not found"));
        if (payload.containsKey("name")) user.setName(payload.get("name"));
        if (payload.containsKey("phone")) user.setPhone(payload.get("phone"));
        if (payload.containsKey("email") && !user.getEmail().equals(payload.get("email"))) {
            if (userRepository.existsByEmail(payload.get("email"))) {
                throw new BadRequestException("Email already in use");
            }
            user.setEmail(payload.get("email"));
        }
        User saved = userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", UserResponse.fromEntity(saved)));
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody Map<String, String> payload, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new BadRequestException("User not found"));
        if (!passwordEncoder.matches(payload.get("currentPassword"), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        String newPassword = payload.get("newPassword");
        if (newPassword == null || newPassword.length() < 6) {
            throw new BadRequestException("New password must be at least 6 characters");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("Password changed", null));
    }

    // ============ APP SETTINGS (KEY-VALUE) ============
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Map<String, List<Setting>>>> getAllSettings() {
        Map<String, List<Setting>> grouped = settingRepository.findAll().stream()
                .collect(Collectors.groupingBy(Setting::getCategory, LinkedHashMap::new, Collectors.toList()));
        return ResponseEntity.ok(ApiResponse.success(grouped));
    }

    @PutMapping("/bulk")
    public ResponseEntity<ApiResponse<Void>> updateSettings(@RequestBody Map<String, String> updates) {
        System.out.println("🔄 Received settings update request: " + updates.keySet());
        
        updates.forEach((key, value) -> {
            var settingOpt = settingRepository.findById(key);
            if (settingOpt.isPresent()) {
                var setting = settingOpt.get();
                setting.setValue(value);
                settingRepository.save(setting);
            } else {
                System.out.println("⚠️ Setting key not found in DB: " + key);
            }
        });
        System.out.println("✅ Settings update completed.");
        return ResponseEntity.ok(ApiResponse.success("Settings updated successfully", null));
    }
}
