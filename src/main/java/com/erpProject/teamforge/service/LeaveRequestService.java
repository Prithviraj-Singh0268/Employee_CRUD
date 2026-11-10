package com.erpProject.teamforge.service;

import com.erpProject.teamforge.dto.LeaveCreateRequest;
import com.erpProject.teamforge.dto.LeaveResponse;
import com.erpProject.teamforge.dto.PageResponse;
import com.erpProject.teamforge.entity.Employee;
import com.erpProject.teamforge.entity.LeaveRequest;
import com.erpProject.teamforge.entity.LeaveStatus;
import com.erpProject.teamforge.exception.ResourceNotFoundException;
import com.erpProject.teamforge.repository.EmployeeRepository;
import com.erpProject.teamforge.repository.LeaveRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository repository;
    private final EmployeeRepository employeeRepository;

    public LeaveRequestService(LeaveRequestRepository repository,
                               EmployeeRepository employeeRepository) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public LeaveResponse create(LeaveCreateRequest req) {
        Employee employee = employeeRepository.findById(req.employeeId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Employee not found with id " + req.employeeId()));

        if (req.endDate().isBefore(req.startDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        long overlaps = repository.countOverlapping(
            employee.getId(),
            List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED),
            req.startDate(), req.endDate());
        if (overlaps > 0) {
            throw new IllegalArgumentException(
                "Employee already has a pending or approved leave overlapping these dates");
        }

        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setLeaveType(req.leaveType());
        leave.setStartDate(req.startDate());
        leave.setEndDate(req.endDate());
        leave.setReason(req.reason());
        leave.setStatus(LeaveStatus.PENDING);
        leave.setCreatedAt(LocalDateTime.now());
        return toResponse(repository.save(leave));
    }

    public PageResponse<LeaveResponse> search(Long employeeId, LeaveStatus status,
                                              int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("page must be >= 0 and size must be between 1 and 100");
        }
        Page<LeaveRequest> result = repository.search(employeeId, status,
            PageRequest.of(page, size, Sort.by("createdAt").descending()));

        return new PageResponse<>(
            result.getContent().stream().map(this::toResponse).toList(),
            result.getNumber(), result.getSize(),
            result.getTotalElements(), result.getTotalPages(), result.isLast());
    }

    public LeaveResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public LeaveResponse approve(Long id) {
        return transition(id, LeaveStatus.APPROVED, EnumSet.of(LeaveStatus.PENDING));
    }

    public LeaveResponse reject(Long id) {
        return transition(id, LeaveStatus.REJECTED, EnumSet.of(LeaveStatus.PENDING));
    }

    public LeaveResponse cancel(Long id) {
        return transition(id, LeaveStatus.CANCELLED,
            EnumSet.of(LeaveStatus.PENDING, LeaveStatus.APPROVED));
    }

    // One method enforces the state machine for all three actions
    @Transactional
    private LeaveResponse transition(Long id, LeaveStatus target, Set<LeaveStatus> allowedFrom) {
        LeaveRequest leave = findOrThrow(id);
        if (!allowedFrom.contains(leave.getStatus())) {
            throw new IllegalStateException(
                "Cannot change leave from " + leave.getStatus() + " to " + target);
        }
        leave.setStatus(target);
        return toResponse(repository.save(leave));
    }

    private LeaveRequest findOrThrow(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id " + id));
    }

    private LeaveResponse toResponse(LeaveRequest l) {
        Employee e = l.getEmployee();
        return new LeaveResponse(
            l.getId(), e.getId(), e.getFirstName() + " " + e.getLastName(),
            l.getLeaveType(), l.getStartDate(), l.getEndDate(),
            ChronoUnit.DAYS.between(l.getStartDate(), l.getEndDate()) + 1,
            l.getReason(), l.getStatus(), l.getCreatedAt());
    }
}