package com.freelance.freelance_api.services;

import com.freelance.freelance_api.dtos.RoleChangeDto;
import com.freelance.freelance_api.dtos.UserUpdateDto;
import com.freelance.freelance_api.entities.Role;
import com.freelance.freelance_api.entities.User;
import com.freelance.freelance_api.repositories.RoleRepository;
import com.freelance.freelance_api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Async
    public CompletableFuture<java.util.List<User>> getAllUsers() {
        return CompletableFuture.completedFuture(userRepository.findAll());
    }

    @Async
    public CompletableFuture<User> getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return CompletableFuture.completedFuture(user);
    }

    @Async
    @Transactional
    public CompletableFuture<User> toggleUserActivity(String username, boolean isActive) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(isActive);
        return CompletableFuture.completedFuture(userRepository.save(user));
    }

    @Async
    @Transactional
    public CompletableFuture<User> updateUser(String usernameToUpdate, UserUpdateDto dto, String currentUsername){
        if(!usernameToUpdate.equals(currentUsername)){
            throw new RuntimeException("You are not authorized to update this user");
        }
        User user = userRepository.findByUsername(usernameToUpdate)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + usernameToUpdate));

        if (dto.getEmail() != null && !dto.getEmail().isEmpty()){
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()){
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if(dto.getBiography()!= null){
            user.setBiography(dto.getBiography());
        }
        return CompletableFuture.completedFuture(userRepository.save(user));
    }

    @Async
    @Transactional
    public CompletableFuture<Void> deleteUser(String usernameToDelete, String currentUsername, boolean isAdmin){
        if (!usernameToDelete.equals(currentUsername) && !isAdmin) {
            throw new RuntimeException("You are not authorized to delete this profile!");
        }

        User user = userRepository.findByUsername(usernameToDelete)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Transactional
    public CompletableFuture<User> changeUserRole(String username, RoleChangeDto dto){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role newRole = roleRepository.findByName(dto.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found: " + dto.getRoleName()));

        user.getRoles().clear();
        user.addRole(newRole);

        return CompletableFuture.completedFuture(userRepository.save(user));
    }
}