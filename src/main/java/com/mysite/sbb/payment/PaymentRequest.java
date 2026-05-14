package com.mysite.sbb.payment;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentRequest {
    private String storeName;
    private String paymentMethod;
    private int totalAmount;
    private List<PaymentItemRequest> items;
}
