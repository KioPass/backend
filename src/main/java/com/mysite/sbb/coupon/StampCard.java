package com.mysite.sbb.coupon;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"buyerEmail", "storeId"}))
public class StampCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String buyerEmail;
    private Long storeId;
    private String storeName;

    private int stampCount;       // 누적 결제 횟수 (10번마다 쿠폰 발급)
    private int availableCoupons; // 사용 가능한 1000원 쿠폰 수
    private int totalEarned;      // 총 발급된 쿠폰 수 (표시용)
}
