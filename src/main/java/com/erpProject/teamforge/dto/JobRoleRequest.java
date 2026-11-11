package com.erpProject.teamforge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record JobRoleRequest(
    @NotBlank String title,
    String description,
    @PositiveOrZero BigDecimal minSalary,
    @PositiveOrZero BigDecimal maxSalary
) {}