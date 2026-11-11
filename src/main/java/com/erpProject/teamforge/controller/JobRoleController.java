package com.erpProject.teamforge.controller;

import com.erpProject.teamforge.dto.JobRoleRequest;
import com.erpProject.teamforge.dto.JobRoleResponse;
import com.erpProject.teamforge.service.JobRoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/job-roles")
public class JobRoleController {

    private final JobRoleService service;

    public JobRoleController(JobRoleService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<JobRoleResponse> create(@Valid @RequestBody JobRoleRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @GetMapping
    public List<JobRoleResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public JobRoleResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public JobRoleResponse update(@PathVariable Long id, @Valid @RequestBody JobRoleRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}