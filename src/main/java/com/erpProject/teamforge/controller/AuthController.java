package com.erpProject.teamforge.controller;

import com.erpProject.teamforge.dto.LoginRequest;
import com.erpProject.teamforge.dto.LoginResponse;
import com.erpProject.teamforge.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return service.login(req);
    }
}