package com.mysite.sbb.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentItemRequest {
    private String productName;
    private String barcode;
    private int quantity;
    private int price;
}
