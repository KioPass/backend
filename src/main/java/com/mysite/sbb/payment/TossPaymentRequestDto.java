package com.mysite.sbb.payment;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TossPaymentRequestDto {
    private Long storeId;
    private String storeName;
    private int amount;
    private String orderName;
    private String tossMethod;  // "카카오페이", "토스페이", "카드"
    private String successUrl;
    private String failUrl;
}
