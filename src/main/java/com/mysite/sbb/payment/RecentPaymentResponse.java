package com.mysite.sbb.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RecentPaymentResponse {
    private Long id;
    private String time;         // "14:23"
    private String buyerName;    // 마스킹 처리 or "비회원"
    private String paymentMethod;
    private String itemSummary;  // 첫 번째 상품명...
    private int totalAmount;
}
