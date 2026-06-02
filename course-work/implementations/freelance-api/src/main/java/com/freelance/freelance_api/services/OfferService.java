package com.freelance.freelance_api.services;

import com.freelance.freelance_api.dtos.OfferRequestDto;
import com.freelance.freelance_api.entities.Category;
import com.freelance.freelance_api.entities.Offer;
import com.freelance.freelance_api.entities.User;
import com.freelance.freelance_api.repositories.CategoryRepository;
import com.freelance.freelance_api.repositories.OfferRepository;
import com.freelance.freelance_api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class OfferService {
    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public OfferService(OfferRepository offerRepository, UserRepository userRepository, CategoryRepository categoryRepository){
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Async
    @Transactional
    public CompletableFuture<Offer> createOffer(OfferRequestDto dto, String username){
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User must be logged in"));
        Offer offer = new Offer();
        offer.setTitle(dto.getTitle());
        offer.setDescription(dto.getDescription());
        offer.setPrice(dto.getPrice());
        offer.setAuthor(author);

        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()){
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(dto.getCategoryIds()));
            offer.setCategories(categories);
        } else {
            offer.setCategories(new HashSet<>());
        }
        return CompletableFuture.completedFuture(offerRepository.save(offer));
    }

    @Async
    public CompletableFuture<Page<Offer>> getAllOffers(String title, BigDecimal maxPrice, int page, int size, String sortBy){

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<Offer> offersPage = offerRepository.findByTitleContainingIgnoreCaseAndPriceLessThanEqual(title, maxPrice, pageable);
        return CompletableFuture.completedFuture(offersPage);
    }

    public Offer getOfferById(Long id){
        return offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found with id: " + id));
    }

    @Async
    @Transactional
    public CompletableFuture<Offer> updateOffer(Long id, OfferRequestDto dto, String currentUsername){
        Offer offer = getOfferById(id);

        if (!offer.getAuthor().getUsername().equals(currentUsername)){
            throw new RuntimeException("You are not authorized to update this offer");
        }
        offer.setTitle(dto.getTitle());
        offer.setDescription(dto.getDescription());
        offer.setPrice(dto.getPrice());

        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()){
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(dto.getCategoryIds()));
            offer.setCategories(categories);
        } else {
            offer.setCategories(new HashSet<>());
        }
        return CompletableFuture.completedFuture(offerRepository.save(offer));
    }

    @Async
    @Transactional
    public CompletableFuture<Void> deleteOffer(Long id, String currentUsername){
        Offer offer = getOfferById(id);
        boolean isAdmin = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getRoles()
                .stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        if (!offer.getAuthor().getUsername().equals(currentUsername ) && !isAdmin){
            throw new RuntimeException("You are not authorized to delete this offer");
        }
        offerRepository.delete(offer);
        return CompletableFuture.completedFuture(null);
    }
    @Async
    public CompletableFuture<Page<Offer>> getAllOffersForCatalog(String title, BigDecimal maxPrice, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<Offer> offersPage = offerRepository
                .findByTitleContainingIgnoreCaseAndPriceLessThanEqualAndAuthorIsActiveTrue(title, maxPrice, pageable);

        return CompletableFuture.completedFuture(offersPage);
    }
    @Async
    public CompletableFuture<Page<Offer>> getMyOffers(String username, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<Offer> myOffersPage = offerRepository.findByAuthorUsername(username, pageable);
        return CompletableFuture.completedFuture(myOffersPage);
    }
}