package com.freelance.freelance_api.controllers;

import com.freelance.freelance_api.dtos.AuthResponse;
import com.freelance.freelance_api.dtos.UserLoginDto;
import com.freelance.freelance_api.dtos.UserRegisterDto;
import com.freelance.freelance_api.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<?>> register(@Valid @RequestBody UserRegisterDto userRegisterDto){
        return authService.register(userRegisterDto)
                .thenApply(user -> ResponseEntity.ok("User registered successfully: " + user.getUsername()));
    }

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<?>> login(@Valid @RequestBody UserLoginDto userLoginDto){
        return authService.login(userLoginDto)
                .thenApply(token -> ResponseEntity.ok(new AuthResponse(token)));
    }
}