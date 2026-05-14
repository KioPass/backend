package com.mysite.sbb.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TopProductResponse {
    private int rank;
    private String productName;
    private int totalCount;
}
