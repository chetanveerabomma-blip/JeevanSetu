package com.jeevansetu.dto;

public class AuthResponse {
    private String token;
    private String email;
    private String role;
    private String userId;
    private String fullName;
    private String unitId;
    private String organization;

    public AuthResponse() {}

    public AuthResponse(String token, String email, String role, String userId, String fullName, String unitId, String organization) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.userId = userId;
        this.fullName = fullName;
        this.unitId = unitId;
        this.organization = organization;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUnitId() { return unitId; }
    public void setUnitId(String unitId) { this.unitId = unitId; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
}
