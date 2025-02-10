package com.example.PhoneShop.repository;

import com.example.PhoneShop.entities.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, String> {
    @Query("SELECT ph FROM PriceHistory ph WHERE ph.productVariant.id = :variantId AND ph.endDate IS NULL")
    Optional<PriceHistory> findCurrentPriceHistoryByProductVariantId(@Param("variantId") Long variantId);
}
