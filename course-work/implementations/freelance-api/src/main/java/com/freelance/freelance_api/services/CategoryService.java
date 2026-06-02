package com.freelance.freelance_api.services;

import com.freelance.freelance_api.dtos.CategoryRequestDto;
import com.freelance.freelance_api.entities.Category;
import com.freelance.freelance_api.entities.User;
import com.freelance.freelance_api.repositories.CategoryRepository;
import com.freelance.freelance_api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Async
    @Transactional
    public CompletableFuture<Category> createCategory(CategoryRequestDto dto, String adminUsername) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        Category category = new Category(dto.getName(), dto.getDescription(), admin);
        return CompletableFuture.completedFuture(categoryRepository.save(category));
    }

    @Async
    public CompletableFuture<List<Category>> getAllCategories() {
        return CompletableFuture.completedFuture(categoryRepository.findAll());
    }

    @Async
    public CompletableFuture<Category> getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return CompletableFuture.completedFuture(category);
    }

    @Async
    @Transactional
    public CompletableFuture<Category> updateCategory(Long id, CategoryRequestDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());

        return CompletableFuture.completedFuture(categoryRepository.save(category));
    }

    @Async
    @Transactional
    public CompletableFuture<Void> deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        categoryRepository.delete(category);
        return CompletableFuture.completedFuture(null);
    }
}