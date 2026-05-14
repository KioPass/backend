package com.mysite.sbb.payment;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TossPaymentConfirmDto {
    private String paymentKey;
    private String orderId;
    private int amount;
    private Long storeId;
    private String storeName;
    private List<PaymentItemRequest> items;
    private boolean couponUsed;
}
