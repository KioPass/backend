package com.mysite.sbb.payment;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.sbb.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping
class BuyerPaymentController {
    private final PaymentService paymentService;

    @GetMapping("/api/user/payments")
    public ResponseEntity<ApiResponse<List<BuyerPaymentResponse>>> getMyPayments(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                paymentService.getBuyerPayments(userDetails.getUsername())));
    }
}

@RestController
@RequestMapping("/api/store/{storeId}")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 생성
    @PostMapping("/payments")
    public ResponseEntity<ApiResponse<Long>> createPayment(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PaymentRequest request) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        Payment payment = paymentService.createPayment(storeId, email, request);
        return ResponseEntity.ok(ApiResponse.success(payment.getId()));
    }

    // 결제 내역 (기간 필터 지원: today, week, month, 3month)
    @GetMapping("/payments/recent")
    public ResponseEntity<ApiResponse<List<RecentPaymentResponse>>> getRecentPayments(
            @PathVariable Long storeId,
            @RequestParam(required = false) String period) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getRecentPayments(storeId, period)));
    }

    // 매출 요약 (오늘 매출 + 전일 대비)
    @GetMapping("/sales/summary")
    public ResponseEntity<ApiResponse<SalesSummaryResponse>> getSalesSummary(
            @PathVariable Long storeId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getSalesSummary(storeId)));
    }

    // 판매량 TOP 5
    @GetMapping("/sales/top")
    public ResponseEntity<ApiResponse<List<TopProductResponse>>> getTopProducts(
            @PathVariable Long storeId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getTopProducts(storeId)));
    }
}
