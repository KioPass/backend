package com.mysite.sbb.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStoreId(Long storeId);
    Optional<Product> findByStoreIdAndBarcode(Long storeId, String barcode);
}
