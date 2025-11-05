package com.edos.Middleware.service.Auth;

import com.edos.Middleware.dto.auth.LoginRequest;
import com.edos.Middleware.dto.auth.LoginResponse;
import jakarta.security.auth.message.AuthException;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);

    LoginResponse refreshToken(String refreshToken) throws AuthException;
}