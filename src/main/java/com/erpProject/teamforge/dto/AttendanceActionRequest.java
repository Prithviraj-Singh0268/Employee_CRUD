package com.erpProject.teamforge.dto;

import jakarta.validation.constraints.NotNull;

public record AttendanceActionRequest(@NotNull Long employeeId) {}