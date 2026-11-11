package com.erpProject.teamforge.service;

import com.erpProject.teamforge.dto.AttendanceResponse;
import com.erpProject.teamforge.dto.PageResponse;
import com.erpProject.teamforge.entity.Attendance;
import com.erpProject.teamforge.entity.Employee;
import com.erpProject.teamforge.entity.LeaveStatus;
import com.erpProject.teamforge.exception.ResourceNotFoundException;
import com.erpProject.teamforge.repository.AttendanceRepository;
import com.erpProject.teamforge.repository.EmployeeRepository;
import com.erpProject.teamforge.repository.LeaveRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository repository;
    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public AttendanceService(AttendanceRepository repository,
                             EmployeeRepository employeeRepository,
                             LeaveRequestRepository leaveRequestRepository) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Transactional
    public AttendanceResponse checkIn(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + employeeId));

        LocalDate today = LocalDate.now();

        if (repository.findByEmployeeIdAndWorkDate(employeeId, today).isPresent()) {
            throw new IllegalStateException("Employee has already checked in today");
        }

        long onLeave = leaveRequestRepository.countOverlapping(
            employeeId, List.of(LeaveStatus.APPROVED), today, today);
        if (onLeave > 0) {
            throw new IllegalStateException("Employee is on approved leave today");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setWorkDate(today);
        attendance.setCheckIn(LocalDateTime.now());
        return toResponse(repository.save(attendance));
    }

    @Transactional
    public AttendanceResponse checkOut(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id " + employeeId);
        }

        Attendance attendance = repository
            .findByEmployeeIdAndWorkDate(employeeId, LocalDate.now())
            .orElseThrow(() -> new IllegalStateException("No check-in found for today"));

        if (attendance.getCheckOut() != null) {
            throw new IllegalStateException("Employee has already checked out today");
        }

        attendance.setCheckOut(LocalDateTime.now());
        return toResponse(repository.save(attendance));
    }

    public PageResponse<AttendanceResponse> search(Long employeeId, LocalDate from, LocalDate to,
                                                   int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("page must be >= 0 and size must be between 1 and 100");
        }
        LocalDate end = (to != null) ? to : LocalDate.now();
        LocalDate start = (from != null) ? from : end.minusDays(30);
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("'from' date cannot be after 'to' date");
        }

        Page<Attendance> result = repository.search(employeeId, start, end,
            PageRequest.of(page, size, Sort.by("workDate").descending()));

        return new PageResponse<>(
            result.getContent().stream().map(this::toResponse).toList(),
            result.getNumber(), result.getSize(),
            result.getTotalElements(), result.getTotalPages(), result.isLast());
    }

    public AttendanceResponse getById(Long id) {
        return toResponse(repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id " + id)));
    }

    private AttendanceResponse toResponse(Attendance a) {
        Employee e = a.getEmployee();
        Double hours = null;
        if (a.getCheckOut() != null) {
            long minutes = Duration.between(a.getCheckIn(), a.getCheckOut()).toMinutes();
            hours = Math.round(minutes / 60.0 * 100) / 100.0;
        }
        return new AttendanceResponse(
            a.getId(), e.getId(), e.getFirstName() + " " + e.getLastName(),
            a.getWorkDate(), a.getCheckIn(), a.getCheckOut(), hours,
            a.getCheckOut() == null ? "CHECKED_IN" : "COMPLETED");
    }
}