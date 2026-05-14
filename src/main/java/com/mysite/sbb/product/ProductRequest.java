package com.mysite.sbb.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductRequest {
    private String barcode;
    private String name;
    private int price;
    private int stock;
    private String category;
}
