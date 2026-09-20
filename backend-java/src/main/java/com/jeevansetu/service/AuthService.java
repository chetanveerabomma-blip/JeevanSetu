package com.jeevansetu.service;

import com.jeevansetu.dto.AuthRequest;
import com.jeevansetu.dto.AuthResponse;
import com.jeevansetu.dto.RegisterRequest;
import com.jeevansetu.exception.BadRequestException;
import com.jeevansetu.exception.ResourceNotFoundException;
import com.jeevansetu.model.Role;
import com.jeevansetu.model.User;
import com.jeevansetu.repository.UserRepository;
import com.jeevansetu.security.JwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditLogService auditLogService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.auditLogService = auditLogService;
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId(),
                user.getFullName()
        );

        auditLogService.logAction(
                user.getEmail(),
                user.getRole().name(),
                "USER_LOGIN",
                null,
                null,
                "Logged in successfully",
                null,
                null
        );

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRole().name(),
                user.getId(),
                user.getFullName(),
                user.getUnitId(),
                user.getOrganization()
        );
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("An account with email " + email + " already exists.");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.CITIZEN;
        if (role == Role.ADMIN || role == Role.CONTROL_CENTER) {
            throw new BadRequestException("Administrative accounts cannot be registered publicly.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setFullName(request.getFullName());
        user.setOrganization(request.getOrganization());
        user.setUnitType(request.getUnitType());
        user.setUnitId(request.getUnitId());
        user.setRegistrationId(request.getRegistrationId());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setEmergencyContact(request.getEmergencyContact());
        user.setCreatedAt(Instant.now());

        User saved = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(
                saved.getEmail(),
                saved.getRole().name(),
                saved.getId(),
                saved.getFullName()
        );

        auditLogService.logAction(
                saved.getEmail(),
                saved.getRole().name(),
                "USER_REGISTERED",
                null,
                null,
                "Registered new " + saved.getRole() + " account",
                null,
                null
        );

        return new AuthResponse(
                token,
                saved.getEmail(),
                saved.getRole().name(),
                saved.getId(),
                saved.getFullName(),
                saved.getUnitId(),
                saved.getOrganization()
        );
    }

    public AuthResponse getDemoAuth(String roleName) {
        Role role;
        try {
            role = Role.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            role = Role.CITIZEN;
        }

        Role finalRole = role;
        User user = userRepository.findByRole(finalRole).stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Demo user for role " + finalRole + " not seeded."));

        String token = jwtTokenProvider.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId(),
                user.getFullName()
        );

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRole().name(),
                user.getId(),
                user.getFullName(),
                user.getUnitId(),
                user.getOrganization()
        );
    }
}
