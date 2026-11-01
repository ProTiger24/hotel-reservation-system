package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.entity.Setting;
import com.yourhotel.booking.entity.User;
import com.yourhotel.booking.repository.SettingRepository;
import com.yourhotel.booking.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/settings")
public class AdminSettingsController {

    private final SettingRepository settingRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSettingsController(SettingRepository settingRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.settingRepository = settingRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 🚀 1. Get All Settings Grouped by Category
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Map<String, List<Setting>>>> getAllSettings() {
        List<Setting> allSettings = settingRepository.findAll();
        
        // ক্যাটাগরি অনুযায়ী গ্রুপ করা হচ্ছে (যেমন GENERAL, BOOKING)
        Map<String, List<Setting>> groupedSettings = allSettings.stream()
                .collect(Collectors.groupingBy(Setting::getCategory));
                
        return ResponseEntity.ok(ApiResponse.success("Settings loaded", groupedSettings));
    }

    // 🚀 2. Save Settings in Bulk
    @PutMapping("/bulk")
    public ResponseEntity<ApiResponse<Void>> updateBulkSettings(@RequestBody Map<String, String> updates) {
        for (Map.Entry<String, String> entry : updates.entrySet()) {
            Optional<Setting> optionalSetting = settingRepository.findById(entry.getKey());
            if (optionalSetting.isPresent()) {
                Setting setting = optionalSetting.get();
                setting.setValue(entry.getValue());
                settingRepository.save(setting);
            }
        }
        return ResponseEntity.ok(ApiResponse.success("Settings updated successfully", null));
    }

    // 🚀 3. Get Admin Profile
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, String>>> getProfile(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, String> profile = new HashMap<>();
        profile.put("name", user.getName());
        profile.put("email", user.getEmail());
        profile.put("phone", user.getPhone() != null ? user.getPhone() : "");
        
        return ResponseEntity.ok(ApiResponse.success("Profile loaded", profile));
    }

    // 🚀 4. Update Profile
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(@RequestBody Map<String, String> payload, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setName(payload.get("name"));
        user.setEmail(payload.get("email"));
        user.setPhone(payload.get("phone"));
        userRepository.save(user);
        
        return ResponseEntity.ok(ApiResponse.success("Profile updated", null));
    }

    // 🚀 5. Update Password
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@RequestBody Map<String, String> payload, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String currentPassword = payload.get("currentPassword");
        String newPassword = payload.get("newPassword");
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Incorrect current password");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        return ResponseEntity.ok(ApiResponse.success("Password updated", null));
    }
}
