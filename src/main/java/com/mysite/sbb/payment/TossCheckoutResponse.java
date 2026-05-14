package com.mysite.sbb.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TossCheckoutResponse {
    private String checkoutUrl;
    private String orderId;
}
