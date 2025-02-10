package com.example.PhoneShop.repository;

import com.example.PhoneShop.entities.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, String> {
    Optional<Discount> findFirstByProductVariant_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
            Long variantId, LocalDateTime currentTime1, LocalDateTime currentTime2);

    List<Discount> findByProductVariant_Id(Long variantId);
}
