package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.dto.AuthRequest;
import com.jeevansetu.dto.AuthResponse;
import com.jeevansetu.dto.RegisterRequest;
import com.jeevansetu.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user login, multi-role registration, and quick demo logins")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email/phone and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user account with role-specific profile")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.ok("Account created successfully", response));
    }

    @GetMapping("/demo/{role}")
    @Operation(summary = "Instant one-click demo login for college evaluations and quick tests")
    public ResponseEntity<ApiResponse<AuthResponse>> demoLogin(@PathVariable String role) {
        AuthResponse response = authService.getDemoAuth(role);
        return ResponseEntity.ok(ApiResponse.ok("Demo login as " + role, response));
    }
}
