package com.freelance.freelance_api.controllers;

import com.freelance.freelance_api.dtos.RoleChangeDto;
import com.freelance.freelance_api.dtos.UserRegisterDto;
import com.freelance.freelance_api.dtos.UserUpdateDto;
import com.freelance.freelance_api.entities.Category;
import com.freelance.freelance_api.entities.Offer;
import com.freelance.freelance_api.entities.User;
import com.freelance.freelance_api.repositories.CategoryRepository;
import com.freelance.freelance_api.services.AuthService;
import com.freelance.freelance_api.services.OfferService;
import com.freelance.freelance_api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/view")
public class AuthViewController {

    private final UserService userService;
    private final AuthService authService;
    private final OfferService offerService;
    private final CategoryRepository catRepo;

    public AuthViewController(UserService userService, AuthService authService, OfferService offerService, CategoryRepository catRepo) {
        this.userService = userService;
        this.authService = authService;
        this.offerService = offerService;
        this.catRepo = catRepo;
    }

    @GetMapping("/login")
    public String showLoginForm(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/view/offers";
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/view/offers";
        }
        model.addAttribute("registerDto", new UserRegisterDto());
        return "register";
}

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("registerDto") UserRegisterDto dto,
                                  BindingResult bindingResult,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            authService.register(dto).join();
            return "redirect:/view/login?success=true";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/profile")
    public String showMyProfile(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/view/login";
        }

        User user = userService.getUserByUsername(authentication.getName()).join();

        model.addAttribute("user", user);
        model.addAttribute("currentUsername", user.getUsername());
        model.addAttribute("userUpdateDto", new UserUpdateDto());

        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("userUpdateDto") UserUpdateDto dto,
                                BindingResult bindingResult,
                                Authentication authentication,
                                Model model) {
        if (authentication == null) {
            return "redirect:/view/login";
        }

        if (bindingResult.hasErrors()) {
            User user = userService.getUserByUsername(authentication.getName()).join();
            model.addAttribute("user", user);
            model.addAttribute("currentUsername", user.getUsername());
            return "profile";
        }

        try {
            userService.updateUser(authentication.getName(), dto, authentication.getName()).join();
            return "redirect:/view/profile?success=updated";
        } catch (Exception e) {
            User user = userService.getUserByUsername(authentication.getName()).join();
            model.addAttribute("user", user);
            model.addAttribute("currentUsername", user.getUsername());
            model.addAttribute("errorMessage", e.getMessage());
            return "profile";
        }
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/view/login";
        }

        userService.deleteUser(authentication.getName(), authentication.getName(), false).join();


        return "redirect:/logout";
    }


    @GetMapping("/admin/panel")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAdminPanel(Model model, Authentication authentication) {
       List<User> allUsers = userService.getAllUsers().join();


        List<Offer> allOffers = offerService.getAllOffers("", BigDecimal.valueOf(999999), 0, 100, "id")
                .join()
                .getContent();


        List<Category> allCategories = catRepo.findAll();

        model.addAttribute("users", allUsers);
        model.addAttribute("offers", allOffers);
        model.addAttribute("categories", allCategories);
        model.addAttribute("currentUsername", authentication.getName());

        return "admin-panel";
    }


    @PostMapping("/admin/users/{username}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public String toggleUserStatus(@PathVariable String username) {
        User user = userService.getUserByUsername(username).join();

        boolean currentStatus = (user.getIsActive() != null) ? user.getIsActive() : false;

        userService.toggleUserActivity(username, !currentStatus).join();
        return "redirect:/view/admin/panel?success=status_changed";
    }

    @PostMapping("/admin/users/{username}/make-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String promoteToAdmin(@PathVariable String username) {
        RoleChangeDto roleChangeDto = new RoleChangeDto();
        roleChangeDto.setRoleName("ROLE_ADMIN");
        userService.changeUserRole(username, roleChangeDto).join();
        return "redirect:/view/admin/panel?success=promoted";
    }


    @PostMapping("/admin/users/{username}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDeleteUser(@PathVariable String username, Authentication authentication) {
        userService.deleteUser(username, authentication.getName(), true).join();
        return "redirect:/view/admin/panel?success=user_deleted";
    }

    @PostMapping("/admin/offers/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDeleteOffer(@PathVariable Long id, Authentication authentication) {
        offerService.deleteOffer(id, authentication.getName()).join();
        return "redirect:/view/admin/panel?success=offer_deleted";
    }

    @PostMapping("/admin/categories/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createCategory(@RequestParam String name, @RequestParam String description, Authentication authentication) {
        try {
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);

            User admin = userService.getUserByUsername(authentication.getName()).join();
            category.setAuthor(admin);

            catRepo.save(category);
            return "redirect:/view/admin/panel?success=category_created";
        } catch (Exception e) {
            return "redirect:/view/admin/panel?error=category_failed";
        }
    }
    @PostMapping("/admin/categories/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCategory(@PathVariable Long id) {
        try {
            catRepo.deleteById(id);
            return "redirect:/view/admin/panel?success=category_deleted";
        } catch (Exception e) {
            return "redirect:/view/admin/panel?error=category_delete_failed";
        }
    }
    @GetMapping("/users/{username}")
    public String showUserProfile(@PathVariable String username, Model model, Authentication authentication) {
        User user = userService.getUserByUsername(username).join();

        model.addAttribute("user", user);
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("currentUsername", authentication.getName());
        }
        return "user-public-profile";
    }

}