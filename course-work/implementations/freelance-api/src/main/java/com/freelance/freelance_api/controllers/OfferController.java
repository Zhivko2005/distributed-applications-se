package com.freelance.freelance_api.controllers;

import com.freelance.freelance_api.dtos.OfferRequestDto;
import com.freelance.freelance_api.entities.Offer;
import com.freelance.freelance_api.services.OfferService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<Offer>> createOffer(@Valid @RequestBody OfferRequestDto dto, Principal principal){
        return offerService.createOffer(dto, principal.getName())
                .thenApply(createdOffer -> new ResponseEntity<>(createdOffer, HttpStatus.CREATED));
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<Page<Offer>>> getAllOffers(
            @RequestParam(defaultValue = "") String title,
            @RequestParam(defaultValue = "99999999") BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        return offerService.getAllOffers(title, maxPrice, page, size, sortBy)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Long id){
        return ResponseEntity.ok(offerService.getOfferById(id));
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<Offer>> updateOffer(@PathVariable Long id, @Valid @RequestBody OfferRequestDto dto, Principal principal) {
        return offerService.updateOffer(id, dto, principal.getName())
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteOffer(@PathVariable Long id, Principal principal){
        return offerService.deleteOffer(id, principal.getName())
                .thenApply(unused -> ResponseEntity.ok("Offer deleted successfully"));
    }
}