package com.erpProject.teamforge.service;

import com.erpProject.teamforge.dto.EmployeeRequest;
import com.erpProject.teamforge.dto.EmployeeResponse;
import com.erpProject.teamforge.entity.Employee;
import com.erpProject.teamforge.exception.ResourceNotFoundException;
import com.erpProject.teamforge.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public EmployeeResponse create(EmployeeRequest req) {
        if (repository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already in use");
        }
        Employee employee = new Employee();
        applyRequest(employee, req);
        return toResponse(repository.save(employee));
    }

    public List<EmployeeResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public EmployeeResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public EmployeeResponse update(Long id, EmployeeRequest req) {
        Employee employee = findOrThrow(id);
        applyRequest(employee, req);
        return toResponse(repository.save(employee));
    }

    public void delete(Long id) {
        repository.delete(findOrThrow(id));
    }

    private Employee findOrThrow(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));
    }

    private void applyRequest(Employee e, EmployeeRequest r) {
        e.setFirstName(r.firstName());
        e.setLastName(r.lastName());
        e.setEmail(r.email());
        e.setJobTitle(r.jobTitle());
        e.setSalary(r.salary());
        e.setHireDate(r.hireDate());
    }

    private EmployeeResponse toResponse(Employee e) {
        return new EmployeeResponse(e.getId(), e.getFirstName(), e.getLastName(),
            e.getEmail(), e.getJobTitle(), e.getSalary(), e.getHireDate());
    }
}