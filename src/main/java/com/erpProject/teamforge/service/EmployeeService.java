package com.erpProject.teamforge.service;

import com.erpProject.teamforge.dto.EmployeeRequest;
import com.erpProject.teamforge.dto.EmployeeResponse;
import com.erpProject.teamforge.entity.Employee;
import com.erpProject.teamforge.exception.ResourceNotFoundException;
import com.erpProject.teamforge.repository.EmployeeRepository;
import com.erpProject.teamforge.entity.Department;
import com.erpProject.teamforge.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import com.erpProject.teamforge.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.Set;
import com.erpProject.teamforge.repository.LeaveRequestRepository;


@Service
public class EmployeeService {
	private static final Set<String> SORTABLE_FIELDS =
		    Set.of("id", "firstName", "lastName", "email", "salary", "hireDate");

	private final EmployeeRepository repository;
	private final DepartmentRepository departmentRepository;
	private final LeaveRequestRepository leaveRequestRepository;

	public EmployeeService(EmployeeRepository repository,
	                       DepartmentRepository departmentRepository,
	                       LeaveRequestRepository leaveRequestRepository) {
	    this.repository = repository;
	    this.departmentRepository = departmentRepository;
	    this.leaveRequestRepository = leaveRequestRepository;
	}

    public EmployeeResponse create(EmployeeRequest req) {
        if (repository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already in use");
        }
        Employee employee = new Employee();
        applyRequest(employee, req);
        return toResponse(repository.save(employee));
    }

    public PageResponse<EmployeeResponse> search(String keyword, Long departmentId,
            int page, int size,
            String sortBy, String direction) {
    		if (!SORTABLE_FIELDS.contains(sortBy)) {
    			throw new IllegalArgumentException("Cannot sort by: " + sortBy);
    		}
    		if (page < 0 || size < 1 || size > 100) {
    			throw new IllegalArgumentException("page must be >= 0 and size must be between 1 and 100");
    		}

    		Sort sort = direction.equalsIgnoreCase("desc")
    		? Sort.by(sortBy).descending()
    		: Sort.by(sortBy).ascending();
    		Pageable pageable = PageRequest.of(page, size, sort);

    		String kw = (keyword == null) ? "" : keyword.trim();
    		Page<Employee> result = repository.search(kw, departmentId, pageable);

    		return new PageResponse<>(
    		result.getContent().stream().map(this::toResponse).toList(),
    		result.getNumber(),
    		result.getSize(),
    		result.getTotalElements(),
    		result.getTotalPages(),
    		result.isLast()
    		);
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
        Employee employee = findOrThrow(id);
        if (leaveRequestRepository.existsByEmployeeId(id)) {
            throw new IllegalArgumentException("Cannot delete an employee who has leave requests");
        }
        repository.delete(employee);
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
        e.setDepartment(findDepartment(r.departmentId()));
    }

    private Department findDepartment(Long id) {
        if (id == null) return null;
        return departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));
    }

    private EmployeeResponse toResponse(Employee e) {
        Department d = e.getDepartment();
        return new EmployeeResponse(e.getId(), e.getFirstName(), e.getLastName(),
            e.getEmail(), e.getJobTitle(), e.getSalary(), e.getHireDate(),
            d != null ? d.getId() : null,
            d != null ? d.getName() : null);
    }
}