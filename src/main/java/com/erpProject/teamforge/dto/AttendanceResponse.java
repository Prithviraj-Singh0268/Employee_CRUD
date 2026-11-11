package com.erpProject.teamforge.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceResponse(
    Long id,
    Long employeeId,
    String employeeName,
    LocalDate workDate,
    LocalDateTime checkIn,
    LocalDateTime checkOut,
    Double hoursWorked,
    String status
) {}