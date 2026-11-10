package com.erpProject.teamforge.dto;

import com.erpProject.teamforge.entity.LeaveStatus;
import com.erpProject.teamforge.entity.LeaveType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LeaveResponse(
    Long id,
    Long employeeId,
    String employeeName,
    LeaveType leaveType,
    LocalDate startDate,
    LocalDate endDate,
    long days,
    String reason,
    LeaveStatus status,
    LocalDateTime createdAt
) {}