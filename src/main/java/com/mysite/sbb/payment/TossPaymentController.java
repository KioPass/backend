package com.mysite.sbb.payment;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.sbb.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments/toss")
@RequiredArgsConstructor
public class TossPaymentController {

    private final TossPaymentService tossPaymentService;

    // 토스 결제 URL 생성
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<TossCheckoutResponse>> requestPayment(
            @RequestBody TossPaymentRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String buyerEmail = userDetails != null ? userDetails.getUsername() : null;
        TossCheckoutResponse response = tossPaymentService.createPayment(request, buyerEmail);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 토스 결제 성공 콜백 → 앱 딥링크로 리다이렉트
    @GetMapping("/callback")
    public void handleSuccess(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam String amount,
            HttpServletResponse response) throws IOException {
        String redirectUrl = "myapp://toss?status=success"
            + "&paymentKey=" + paymentKey
            + "&orderId=" + orderId
            + "&amount=" + amount;
        response.sendRedirect(redirectUrl);
    }

    // 토스 결제 실패 콜백 → 앱 딥링크로 리다이렉트
    @GetMapping("/fail")
    public void handleFail(
            @RequestParam String code,
            @RequestParam String message,
            @RequestParam(required = false) String orderId,
            HttpServletResponse response) throws IOException {
        String redirectUrl = "myapp://toss?status=fail"
            + "&code=" + code
            + "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);
    }

    // 토스 최종 승인 + 결제 기록 저장
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmPayment(
            @RequestBody TossPaymentConfirmDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String buyerEmail = userDetails != null ? userDetails.getUsername() : null;
        tossPaymentService.confirmPayment(dto, buyerEmail);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
