package com.mysite.sbb.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String barcode;
    private String name;
    private int price;
    private int stock;
    private String category;
    private boolean lowStock;
    private String imageUrl;
}
