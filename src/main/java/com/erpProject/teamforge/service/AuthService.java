package com.erpProject.teamforge.service;

import com.erpProject.teamforge.dto.LoginRequest;
import com.erpProject.teamforge.dto.LoginResponse;
import com.erpProject.teamforge.entity.AppUser;
import com.erpProject.teamforge.repository.UserRepository;
import com.erpProject.teamforge.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest req) {
        // throws an AuthenticationException if the username or password is wrong
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.username(), req.password()));

        AppUser user = userRepository.findByUsername(req.username())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, "Bearer", user.getUsername(),
            user.getRole().name(), jwtService.getExpirationSeconds());
    }
}