package com.freelance.freelance_api.controllers;

import com.freelance.freelance_api.dtos.OfferRequestDto;
import com.freelance.freelance_api.entities.Category;
import com.freelance.freelance_api.entities.Offer;
import com.freelance.freelance_api.repositories.CategoryRepository;
import com.freelance.freelance_api.services.CategoryService;
import com.freelance.freelance_api.services.OfferService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/view/offers")
public class OfferViewController {

    private final OfferService offerService;
    private final CategoryService categoryService;
    private final CategoryRepository catRepo;

    public OfferViewController(OfferService offerService, CategoryService categoryService, CategoryRepository catRepo) {
        this.offerService = offerService;
        this.categoryService = categoryService;
        this.catRepo=catRepo;
    }

    @GetMapping
    public String displayOffersPage(
            @RequestParam(defaultValue = "") String title,
            @RequestParam(defaultValue = "99999999") BigDecimal maxPrice,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id") String sortBy,
            Model model,
            Authentication authentication) {

        Page<Offer> offersPage = offerService.getAllOffersForCatalog(title, maxPrice, page, 6, sortBy).join();


        model.addAttribute("categories", catRepo.findAll());
        model.addAttribute("selectedCategories", categoryIds);
        model.addAttribute("offers", offersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", offersPage.getTotalPages());
        model.addAttribute("titleSearch", title);
        model.addAttribute("maxPriceSearch", maxPrice.intValue() == 99999999 ? "" : maxPrice);
        model.addAttribute("currentSort", sortBy);

        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("currentUsername", authentication.getName());
        }
        return "offers";
    }

    @GetMapping("/{id}")
    public String displayOfferDetails(@PathVariable Long id, Model model, Authentication authentication) {
        Offer offer = offerService.getOfferById(id);
        model.addAttribute("offer", offer);

        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("currentUsername", authentication.getName());
        }
        return "offer-details";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/view/login";
        }

        List<Category> categories = categoryService.getAllCategories().join();

        model.addAttribute("offerDto", new OfferRequestDto());
        model.addAttribute("categories", categories);
        model.addAttribute("currentUsername", authentication.getName());
        return "offer-create";
    }

    @PostMapping("/create")
    public String processCreateOffer(@ModelAttribute("offerDto") OfferRequestDto dto, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/view/login";
        }

        offerService.createOffer(dto, authentication.getName()).join();
        return "redirect:/view/offers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        Offer offer = offerService.getOfferById(id);

        if (authentication == null || !offer.getAuthor().getUsername().equals(authentication.getName())) {
            return "redirect:/view/offers?error=unauthorized";
        }

        OfferRequestDto dto = new OfferRequestDto();
        dto.setTitle(offer.getTitle());
        dto.setDescription(offer.getDescription());
        dto.setPrice(offer.getPrice());

        List<Category> categories = categoryService.getAllCategories().join();

        model.addAttribute("offerDto", dto);
        model.addAttribute("offerId", id);
        model.addAttribute("categories", categories);
        model.addAttribute("currentUsername", authentication.getName());
        return "offer-edit";
    }

    @PostMapping("/edit/{id}")
    public String processUpdateOffer(@PathVariable Long id, @ModelAttribute("offerDto") OfferRequestDto dto, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/view/login";
        }

        offerService.updateOffer(id, dto, authentication.getName()).join();
        return "redirect:/view/offers/" + id;
    }

    @PostMapping("/delete/{id}")
    public String processDeleteOffer(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/view/login";
        }

        offerService.deleteOffer(id, authentication.getName()).join();
        return "redirect:/view/offers";
    }
    @GetMapping("/my-offers")
    public String showMyOffers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            Model model) {

        if (userDetails == null) {
            return "redirect:/view/login";
        }

        String currentUsername = userDetails.getUsername();
        Page<Offer> myOffers = offerService.getMyOffers(currentUsername, page, size, sortBy).join();

        model.addAttribute("myOffers", myOffers);
        model.addAttribute("currentUsername", currentUsername);
        return "my-offers";
    }
}