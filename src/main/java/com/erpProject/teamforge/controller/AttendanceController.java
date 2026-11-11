package com.erpProject.teamforge.controller;

import com.erpProject.teamforge.dto.AttendanceActionRequest;
import com.erpProject.teamforge.dto.AttendanceResponse;
import com.erpProject.teamforge.dto.PageResponse;
import com.erpProject.teamforge.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @PostMapping("/check-in")
    public ResponseEntity<AttendanceResponse> checkIn(@Valid @RequestBody AttendanceActionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.checkIn(req.employeeId()));
    }

    @PostMapping("/check-out")
    public AttendanceResponse checkOut(@Valid @RequestBody AttendanceActionRequest req) {
        return service.checkOut(req.employeeId());
    }

    @GetMapping
    public PageResponse<AttendanceResponse> getAll(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.search(employeeId, from, to, page, size);
    }

    @GetMapping("/{id}")
    public AttendanceResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }
}