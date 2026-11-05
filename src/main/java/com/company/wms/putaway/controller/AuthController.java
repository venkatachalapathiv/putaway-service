package com.company.wms.putaway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.company.wms.putaway.dto.LoginRequest;
import com.company.wms.putaway.dto.LoginResponse;
import com.company.wms.putaway.security.JwtService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final long expiration;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            @Value("${security.jwt.expiration}") long expiration) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.expiration = expiration;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );

        String username = authentication.getName();

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .get()
                .getAuthority()
                .replace("ROLE_", "");

        String token = jwtService.generateToken(username, role);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new LoginResponse(
                        token,
                        "Bearer",
                        expiration
                ));
    }
}