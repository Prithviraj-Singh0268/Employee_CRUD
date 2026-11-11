package com.erpProject.teamforge.dto;

import java.math.BigDecimal;

public record JobRoleResponse(
    Long id,
    String title,
    String description,
    BigDecimal minSalary,
    BigDecimal maxSalary
) {}