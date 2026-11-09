package com.erpProject.teamforge.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @Email @NotBlank String email,
    String jobTitle,
    @PositiveOrZero BigDecimal salary,
    LocalDate hireDate,
    Long departmentId
) {}
