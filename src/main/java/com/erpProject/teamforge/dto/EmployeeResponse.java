package com.erpProject.teamforge.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    Long jobRoleId,
    String jobTitle,
    BigDecimal salary,
    LocalDate hireDate,
    Long departmentId,
    String departmentName
) {}