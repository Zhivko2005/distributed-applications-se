package com.freelance.freelance_api.controllers;

import com.freelance.freelance_api.dtos.RoleChangeDto;
import com.freelance.freelance_api.dtos.UserUpdateDto;
import com.freelance.freelance_api.entities.User;
import com.freelance.freelance_api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public CompletableFuture<ResponseEntity<User>> getUserProfile(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/{username}")
    public CompletableFuture<ResponseEntity<User>> updateUser(@PathVariable String username,
                                                              @Valid @RequestBody UserUpdateDto dto,
                                                              Authentication authentication) {
        return userService.updateUser(username, dto, authentication.getName())
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{username}")
    public CompletableFuture<ResponseEntity<String>> deleteUser(@PathVariable String username, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        return userService.deleteUser(username, authentication.getName(), isAdmin)
                .thenApply(unused -> ResponseEntity.ok("User profile deleted successfully"));
    }

    @PutMapping("/{username}/role")
    public CompletableFuture<ResponseEntity<User>> changeUserRole(@PathVariable String username,
                                                                  @Valid @RequestBody RoleChangeDto dto) {
        return userService.changeUserRole(username, dto)
                .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/{username}/status")
    public CompletableFuture<ResponseEntity<User>> toggleUserStatus(@PathVariable String username,
                                                                    @RequestParam boolean active) {
        return userService.toggleUserActivity(username, active)
                .thenApply(ResponseEntity::ok);
    }
}