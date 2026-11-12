package com.erpProject.teamforge.dto;

import com.erpProject.teamforge.entity.Role;

public record UserResponse(Long id, String username, Role role, boolean enabled, Long employeeId) {}