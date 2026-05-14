package com.mysite.sbb.payment;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mysite.sbb.coupon.StampCardService;
import com.mysite.sbb.notification.NotificationService;
import com.mysite.sbb.product.ProductRepository;
import com.mysite.sbb.store.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    @Value("${toss.client-key}")
    private String clientKey;

    @Value("${toss.secret-key}")
    private String secretKey;

    private final PaymentService paymentService;
    private final StampCardService stampCardService;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final NotificationService notificationService;

    @SuppressWarnings("unchecked")
    public TossCheckoutResponse createPayment(TossPaymentRequestDto request, String buyerEmail) {
        String orderId = "kiopass-" + System.currentTimeMillis();

        String credentials = Base64.getEncoder().encodeToString((clientKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + credentials);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();

        String tossMethod = request.getTossMethod();
        String endpoint;
        if ("카드".equals(tossMethod)) {
            endpoint = "https://api.tosspayments.com/v1/payments";
            body.put("method", "카드");
        } else {
            endpoint = "https://api.tosspayments.com/v1/payments";
            body.put("method", "간편결제");
        }

        body.put("amount", request.getAmount());
        body.put("orderId", orderId);
        body.put("orderName", request.getOrderName());
        body.put("successUrl", request.getSuccessUrl());
        body.put("failUrl", request.getFailUrl());
        if (buyerEmail != null) body.put("customerEmail", buyerEmail);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        System.out.println("[Toss] endpoint: " + endpoint);
        System.out.println("[Toss] body: " + body);

        ResponseEntity<Map> response;
        try {
            response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, Map.class);
        } catch (org.springframework.web.client.HttpClientErrorException |
                 org.springframework.web.client.HttpServerErrorException e) {
            System.out.println("[Toss] 에러: " + e.getResponseBodyAsString());
            throw e;
        }

        System.out.println("[Toss] 응답: " + response.getBody());
        Map<String, Object> responseBody = response.getBody();
        Map<String, Object> checkout = (Map<String, Object>) responseBody.get("checkout");
        String checkoutUrl = (String) checkout.get("url");

        return TossCheckoutResponse.builder()
            .checkoutUrl(checkoutUrl)
            .orderId(orderId)
            .build();
    }

    @SuppressWarnings("unchecked")
    public void confirmPayment(TossPaymentConfirmDto dto, String buyerEmail) {
        String credentials = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + credentials);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", dto.getPaymentKey());
        body.put("orderId", dto.getOrderId());
        body.put("amount", dto.getAmount());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.exchange(
            "https://api.tosspayments.com/v1/payments/confirm",
            HttpMethod.POST,
            entity,
            Map.class
        );

        // 결제 기록 저장
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setStoreName(dto.getStoreName());
        paymentRequest.setPaymentMethod("토스페이");
        paymentRequest.setTotalAmount(dto.getAmount());
        paymentRequest.setItems(dto.getItems());
        paymentService.createPayment(dto.getStoreId(), buyerEmail, paymentRequest, dto.isCouponUsed());

        // 쿠폰 사용 시 차감
        if (dto.isCouponUsed() && buyerEmail != null && dto.getStoreId() != null) {
            try { stampCardService.useCoupon(buyerEmail, dto.getStoreId()); } catch (Exception ignored) {}
        }

        // 재고 차감
        if (dto.getItems() != null && dto.getStoreId() != null) {
            for (PaymentItemRequest item : dto.getItems()) {
                if (item.getBarcode() != null && !item.getBarcode().isEmpty()) {
                    productRepository.findByStoreIdAndBarcode(dto.getStoreId(), item.getBarcode())
                        .ifPresent(product -> {
                            int newStock = Math.max(0, product.getStock() - item.getQuantity());
                            product.setStock(newStock);
                            productRepository.save(product);
                        });
                }
            }
        }

        // 판매자에게 새 주문 알림
        storeRepository.findById(dto.getStoreId()).ifPresent(store -> {
            String sellerEmail = store.getUserEmail();
            int itemCount = dto.getItems() != null ? dto.getItems().size() : 0;
            notificationService.send(sellerEmail,
                    "새 주문이 들어왔어요 🛍️",
                    dto.getStoreName() + "에 " + itemCount + "개 상품 주문 · " + dto.getAmount() + "원");
        });
    }
}
