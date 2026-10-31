package com.yourhotel.booking.service;

import com.yourhotel.booking.dto.request.AdminRegisterRequest;
import com.yourhotel.booking.dto.request.LoginRequest;
import com.yourhotel.booking.dto.request.RegisterRequest;
import com.yourhotel.booking.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse registerAdmin(AdminRegisterRequest request);
    AuthResponse login(LoginRequest request);
}
