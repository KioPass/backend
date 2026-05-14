package com.mysite.sbb.payment;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BuyerPaymentResponse {
    private Long id;
    private String date;        // "2026.04.19"
    private String time;        // "14:23"
    private String storeName;
    private String paymentMethod;
    private int totalAmount;
    private List<ItemInfo> items;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ItemInfo {
        private String name;
        private int quantity;
        private int price;
    }
}
