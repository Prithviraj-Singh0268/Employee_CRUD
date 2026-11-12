package com.erpProject.teamforge.service;

import com.erpProject.teamforge.dto.CreateUserRequest;
import com.erpProject.teamforge.dto.UserResponse;
import com.erpProject.teamforge.entity.AppUser;
import com.erpProject.teamforge.entity.Employee;
import com.erpProject.teamforge.entity.Role;
import com.erpProject.teamforge.exception.ResourceNotFoundException;
import com.erpProject.teamforge.repository.EmployeeRepository;
import com.erpProject.teamforge.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       EmployeeRepository employeeRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(CreateUserRequest req) {
        String username = req.username().trim();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }

        Employee employee = null;
        if (req.employeeId() != null) {
            employee = employeeRepository.findById(req.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Employee not found with id " + req.employeeId()));
            if (userRepository.existsByEmployeeId(req.employeeId())) {
                throw new IllegalArgumentException("This employee already has an account");
            }
        }

        if (req.role() != Role.ADMIN && employee == null) {
            throw new IllegalArgumentException(
                "MANAGER and EMPLOYEE accounts must be linked to an employee (employeeId)");
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setRole(req.role());
        user.setEmployee(employee);
        return toResponse(userRepository.save(user));
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse getByUsername(String username) {
        return toResponse(userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    private UserResponse toResponse(AppUser u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getRole(), u.isEnabled(),
            u.getEmployee() != null ? u.getEmployee().getId() : null);
    }
}