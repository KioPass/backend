package com.mysite.sbb.store;

import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class SellerApplicationResponse {
    private Long storeId;
    private String storeName;
    private String address;
    private String userEmail;
    private String userName;
    private String licenseUrl;
    private String reportUrl;
    private String status;
    private String appliedAt;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    public static SellerApplicationResponse from(Store store, String userName) {
        return SellerApplicationResponse.builder()
                .storeId(store.getId())
                .storeName(store.getStorename())
                .address(store.getAddress())
                .userEmail(store.getUserEmail())
                .userName(userName)
                .licenseUrl(store.getLicenseUrl())
                .reportUrl(store.getReportUrl())
                .status(store.getStatus())
                .appliedAt(store.getCreatedAt() != null ? store.getCreatedAt().format(FMT) : "")
                .build();
    }
}
