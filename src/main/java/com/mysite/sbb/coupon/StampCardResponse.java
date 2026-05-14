package com.mysite.sbb.coupon;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StampCardResponse {
    private Long storeId;
    private String storeName;
    private int stampCount;        // 현재 스탬프 (0~9)
    private int availableCoupons;  // 사용 가능한 쿠폰 수
    private int totalEarned;

    public static StampCardResponse from(StampCard sc) {
        return StampCardResponse.builder()
                .storeId(sc.getStoreId())
                .storeName(sc.getStoreName())
                .stampCount(sc.getStampCount() % 10)
                .availableCoupons(sc.getAvailableCoupons())
                .totalEarned(sc.getTotalEarned())
                .build();
    }
}
