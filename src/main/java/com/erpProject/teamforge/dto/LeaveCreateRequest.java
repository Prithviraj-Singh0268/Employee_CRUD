package com.erpProject.teamforge.dto;

import com.erpProject.teamforge.entity.LeaveType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record LeaveCreateRequest(
    @NotNull Long employeeId,
    @NotNull LeaveType leaveType,
    @NotNull @FutureOrPresent LocalDate startDate,
    @NotNull LocalDate endDate,
    @Size(max = 255) String reason
) {}