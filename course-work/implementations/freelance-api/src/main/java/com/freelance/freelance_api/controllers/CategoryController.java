package com.freelance.freelance_api.controllers;

import com.freelance.freelance_api.dtos.CategoryRequestDto;
import com.freelance.freelance_api.entities.Category;
import com.freelance.freelance_api.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<Category>> createCategory(@Valid @RequestBody CategoryRequestDto dto, Principal principal) {
        return categoryService.createCategory(dto, principal.getName())
                .thenApply(category -> new ResponseEntity<>(category, HttpStatus.CREATED));
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<Category>>> getAllCategories() {
        return categoryService.getAllCategories()
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<Category>> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<Category>> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDto dto) {
        return categoryService.updateCategory(id, dto)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id)
                .thenApply(unused -> ResponseEntity.ok("Category deleted successfully"));
    }
}