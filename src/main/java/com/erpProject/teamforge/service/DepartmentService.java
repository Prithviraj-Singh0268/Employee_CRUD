package com.erpProject.teamforge.service;

import com.erpProject.teamforge.dto.DepartmentRequest;
import com.erpProject.teamforge.dto.DepartmentResponse;
import com.erpProject.teamforge.entity.Department;
import com.erpProject.teamforge.exception.ResourceNotFoundException;
import com.erpProject.teamforge.repository.DepartmentRepository;
import com.erpProject.teamforge.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository repository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository repository, EmployeeRepository employeeRepository) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
    }

    public DepartmentResponse create(DepartmentRequest req) {
        if (repository.existsByName(req.name())) {
            throw new IllegalArgumentException("Department name already exists");
        }
        Department department = new Department();
        department.setName(req.name());
        department.setDescription(req.description());
        return toResponse(repository.save(department));
    }

    public List<DepartmentResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public DepartmentResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public DepartmentResponse update(Long id, DepartmentRequest req) {
        Department department = findOrThrow(id);
        if (!department.getName().equals(req.name()) && repository.existsByName(req.name())) {
            throw new IllegalArgumentException("Department name already exists");
        }
        department.setName(req.name());
        department.setDescription(req.description());
        return toResponse(repository.save(department));
    }

    public void delete(Long id) {
        Department department = findOrThrow(id);
        if (employeeRepository.existsByDepartmentId(id)) {
            throw new IllegalArgumentException("Cannot delete a department that still has employees");
        }
        repository.delete(department);
    }

    private Department findOrThrow(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));
    }

    private DepartmentResponse toResponse(Department d) {
        return new DepartmentResponse(d.getId(), d.getName(), d.getDescription());
    }
}