package com.mysite.sbb.coupon;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StampCardRepository extends JpaRepository<StampCard, Long> {
    Optional<StampCard> findByBuyerEmailAndStoreId(String buyerEmail, Long storeId);
    List<StampCard> findByBuyerEmailOrderByStoreNameAsc(String buyerEmail);
}
