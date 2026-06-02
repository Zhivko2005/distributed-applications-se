package com.freelance.freelance_api.repositories;

import com.freelance.freelance_api.entities.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    Optional<Offer> findByAuthorId(Long authorId);
    Optional<Offer> findByTitle (String title);
    Page<Offer> findByTitleContainingIgnoreCaseAndPriceLessThanEqual(
            String title, BigDecimal maxPrice, Pageable pageable);
    Page<Offer> findByTitleContainingIgnoreCaseAndPriceLessThanEqualAndAuthorIsActiveTrue(
            String title, BigDecimal maxPrice, Pageable pageable);
    Page<Offer> findByAuthorUsername(String username, Pageable pageable);


}
