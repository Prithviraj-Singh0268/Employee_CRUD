package com.erpProject.teamforge.service;

import com.erpProject.teamforge.dto.JobRoleRequest;
import com.erpProject.teamforge.dto.JobRoleResponse;
import com.erpProject.teamforge.entity.JobRole;
import com.erpProject.teamforge.exception.ResourceNotFoundException;
import com.erpProject.teamforge.repository.EmployeeRepository;
import com.erpProject.teamforge.repository.JobRoleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JobRoleService {

    private final JobRoleRepository repository;
    private final EmployeeRepository employeeRepository;

    public JobRoleService(JobRoleRepository repository, EmployeeRepository employeeRepository) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
    }

    public JobRoleResponse create(JobRoleRequest req) {
        validateBand(req);
        String title = req.title().trim();
        if (repository.existsByTitleIgnoreCase(title)) {
            throw new IllegalArgumentException("Job role already exists: " + title);
        }
        JobRole role = new JobRole();
        apply(role, req);
        return toResponse(repository.save(role));
    }

    public List<JobRoleResponse> getAll() {
        return repository.findAll(Sort.by("title")).stream().map(this::toResponse).toList();
    }

    public JobRoleResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public JobRoleResponse update(Long id, JobRoleRequest req) {
        JobRole role = findOrThrow(id);
        validateBand(req);
        String title = req.title().trim();
        if (!role.getTitle().equalsIgnoreCase(title) && repository.existsByTitleIgnoreCase(title)) {
            throw new IllegalArgumentException("Job role already exists: " + title);
        }
        apply(role, req);
        return toResponse(repository.save(role));
    }

    public void delete(Long id) {
        JobRole role = findOrThrow(id);
        if (employeeRepository.existsByJobRoleId(id)) {
            throw new IllegalArgumentException("Cannot delete a job role that employees still hold");
        }
        repository.delete(role);
    }

    private void validateBand(JobRoleRequest req) {
        if (req.minSalary() != null && req.maxSalary() != null
                && req.minSalary().compareTo(req.maxSalary()) > 0) {
            throw new IllegalArgumentException("Minimum salary cannot exceed maximum salary");
        }
    }

    private void apply(JobRole role, JobRoleRequest req) {
        role.setTitle(req.title().trim());
        role.setDescription(req.description());
        role.setMinSalary(req.minSalary());
        role.setMaxSalary(req.maxSalary());
    }

    private JobRole findOrThrow(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Job role not found with id " + id));
    }

    private JobRoleResponse toResponse(JobRole r) {
        return new JobRoleResponse(r.getId(), r.getTitle(), r.getDescription(),
            r.getMinSalary(), r.getMaxSalary());
    }
}