package com.erpProject.teamforge.controller;

import com.erpProject.teamforge.dto.LeaveCreateRequest;
import com.erpProject.teamforge.dto.LeaveResponse;
import com.erpProject.teamforge.dto.PageResponse;
import com.erpProject.teamforge.entity.LeaveStatus;
import com.erpProject.teamforge.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    private final LeaveRequestService service;

    public LeaveRequestController(LeaveRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LeaveResponse> create(@Valid @RequestBody LeaveCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @GetMapping
    public PageResponse<LeaveResponse> getAll(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) LeaveStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.search(employeeId, status, page, size);
    }

    @GetMapping("/{id}")
    public LeaveResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PatchMapping("/{id}/approve")
    public LeaveResponse approve(@PathVariable Long id) {
        return service.approve(id);
    }

    @PatchMapping("/{id}/reject")
    public LeaveResponse reject(@PathVariable Long id) {
        return service.reject(id);
    }

    @PatchMapping("/{id}/cancel")
    public LeaveResponse cancel(@PathVariable Long id) {
        return service.cancel(id);
    }
}