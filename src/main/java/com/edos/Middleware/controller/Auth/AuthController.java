package com.edos.Middleware.controller.Auth;

import com.edos.Middleware.config.SecurityUtils;
import com.edos.Middleware.dto.User.CurrentUser;
import com.edos.Middleware.dto.auth.LoginRequest;
import com.edos.Middleware.dto.auth.LoginResponse;
import com.edos.Middleware.dto.auth.RefreshTokenRequest;
import com.edos.Middleware.entity.User.Employee;
import com.edos.Middleware.service.Auth.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SecurityUtils securityUtils;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(loginRequest);
        
        // Add cookies
        addTokenCookies(response, loginResponse.getAccessToken(), loginResponse.getRefreshToken());

        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest refreshTokenRequest,
            HttpServletRequest request,
            HttpServletResponse response) throws AuthException {
        
        String refreshToken = null;
        if (refreshTokenRequest != null && refreshTokenRequest.getRefreshToken() != null) {
            refreshToken = refreshTokenRequest.getRefreshToken();
        } else if (request.getCookies() != null) {
            refreshToken = Arrays.stream(request.getCookies())
                    .filter(c -> "refreshToken".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (refreshToken == null) {
            throw new AuthException("Refresh token missing.");
        }

        LoginResponse loginResponse = authService.refreshToken(refreshToken);
        
        // Update cookies
        addTokenCookies(response, loginResponse.getAccessToken(), loginResponse.getRefreshToken());

        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Employee authenticatedUser = securityUtils.getAuthenticatedUser();
            CurrentUser currentUser = new CurrentUser();
            currentUser.setId(authenticatedUser.getId());
            currentUser.setName(authenticatedUser.getName());
            currentUser.setEmail(authenticatedUser.getEmail());
            currentUser.setRoles(authenticatedUser.getRoles());
            return ResponseEntity.ok(currentUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        clearTokenCookies(response);
        return ResponseEntity.ok().build();
    }

    private void addTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true); // Must be true for SameSite=None
        accessCookie.setPath("/");
        accessCookie.setAttribute("SameSite", "None"); // Required for cross-domain (Vercel -> Render)
        accessCookie.setMaxAge(3600); // 1 hour

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); 
        refreshCookie.setPath("/");
        refreshCookie.setAttribute("SameSite", "None");
        refreshCookie.setMaxAge(86400 * 7); // 7 days

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    private void clearTokenCookies(HttpServletResponse response) {
        Cookie accessCookie = new Cookie("accessToken", null);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }
}
