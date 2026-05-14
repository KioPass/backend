package com.mysite.sbb.payment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByStoreIdOrderByCreatedAtDesc(Long storeId);

    List<Payment> findByStoreIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long storeId, LocalDateTime start, LocalDateTime end);

    List<Payment> findByBuyerEmailOrderByCreatedAtDesc(String buyerEmail);

    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p " +
           "WHERE p.storeId = :storeId AND p.createdAt >= :start AND p.createdAt < :end")
    int sumAmountByStoreIdAndPeriod(@Param("storeId") Long storeId,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    @Query("SELECT pi.productName, SUM(pi.quantity) as total " +
           "FROM Payment p JOIN p.items pi " +
           "WHERE p.storeId = :storeId " +
           "GROUP BY pi.productName ORDER BY total DESC")
    List<Object[]> findTopProducts(@Param("storeId") Long storeId);
}
