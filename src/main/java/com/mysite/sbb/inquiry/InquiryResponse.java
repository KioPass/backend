package com.mysite.sbb.inquiry;

import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class InquiryResponse {
    private Long id;
    private String userEmail;
    private String userName;
    private String category;
    private String content;
    private String status;
    private String answer;
    private String createdAt;
    private String answeredAt;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    public static InquiryResponse from(Inquiry inquiry) {
        return InquiryResponse.builder()
                .id(inquiry.getId())
                .userEmail(inquiry.getUserEmail())
                .userName(inquiry.getUserName())
                .category(inquiry.getCategory())
                .content(inquiry.getContent())
                .status(inquiry.getStatus())
                .answer(inquiry.getAnswer())
                .createdAt(inquiry.getCreatedAt() != null ? inquiry.getCreatedAt().format(FMT) : "")
                .answeredAt(inquiry.getAnsweredAt() != null ? inquiry.getAnsweredAt().format(FMT) : null)
                .build();
    }
}
