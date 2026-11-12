package com.erpProject.teamforge.dto;

public record LoginResponse(
    String token,
    String tokenType,
    String username,
    String role,
    long expiresInSeconds
) {}