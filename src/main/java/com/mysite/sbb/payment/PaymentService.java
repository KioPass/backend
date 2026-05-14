package com.mysite.sbb.payment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mysite.sbb.coupon.StampCardService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StampCardService stampCardService;

    public Payment createPayment(Long storeId, String buyerEmail, PaymentRequest request) {
        return createPayment(storeId, buyerEmail, request, false);
    }

    public Payment createPayment(Long storeId, String buyerEmail, PaymentRequest request, boolean couponUsed) {
        Payment payment = new Payment();
        payment.setStoreId(storeId);
        payment.setStoreName(request.getStoreName());
        payment.setBuyerEmail(buyerEmail);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTotalAmount(request.getTotalAmount());
        payment.setCreatedAt(LocalDateTime.now());

        List<PaymentItem> items = request.getItems().stream().map(req -> {
            PaymentItem item = new PaymentItem();
            item.setProductName(req.getProductName());
            item.setBarcode(req.getBarcode());
            item.setQuantity(req.getQuantity());
            item.setPrice(req.getPrice());
            return item;
        }).collect(Collectors.toList());
        payment.setItems(items);

        Payment saved = paymentRepository.save(payment);

        // 스탬프 적립 (회원 결제만, 쿠폰 사용 시 제외)
        if (!couponUsed) {
            stampCardService.addStamp(buyerEmail, storeId, request.getStoreName());
        }

        return saved;
    }

    public List<RecentPaymentResponse> getRecentPayments(Long storeId, String period) {
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        switch (period == null ? "today" : period) {
            case "week"  -> start = today.minusDays(7).atStartOfDay();
            case "month" -> start = today.withDayOfMonth(1).atStartOfDay();
            case "3month"-> start = today.minusMonths(3).atStartOfDay();
            default      -> start = today.atStartOfDay(); // today
        }
        List<Payment> payments = period == null
                ? paymentRepository.findByStoreIdOrderByCreatedAtDesc(storeId)
                : paymentRepository.findByStoreIdAndCreatedAtBetweenOrderByCreatedAtDesc(storeId, start, end);
        return payments.stream().limit(50)
                .map(p -> {
                    String buyerName = p.getBuyerEmail() == null ? "비회원"
                            : maskEmail(p.getBuyerEmail());
                    String itemSummary = p.getItems().isEmpty() ? "-"
                            : p.getItems().get(0).getProductName() +
                              (p.getItems().size() > 1 ? " 외 " + (p.getItems().size() - 1) + "건" : "");
                    return RecentPaymentResponse.builder()
                            .id(p.getId())
                            .time(p.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")))
                            .buyerName(buyerName)
                            .paymentMethod(p.getPaymentMethod())
                            .itemSummary(itemSummary)
                            .totalAmount(p.getTotalAmount())
                            .build();
                }).collect(Collectors.toList());
    }

    public List<BuyerPaymentResponse> getBuyerPayments(String email) {
        return paymentRepository.findByBuyerEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(p -> BuyerPaymentResponse.builder()
                        .id(p.getId())
                        .date(p.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                        .time(p.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")))
                        .storeName(p.getStoreName() != null ? p.getStoreName() : "-")
                        .paymentMethod(p.getPaymentMethod())
                        .totalAmount(p.getTotalAmount())
                        .items(p.getItems().stream()
                                .map(i -> BuyerPaymentResponse.ItemInfo.builder()
                                        .name(i.getProductName())
                                        .quantity(i.getQuantity())
                                        .price(i.getPrice())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    public SalesSummaryResponse getSalesSummary(Long storeId) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        int todayAmount = paymentRepository.sumAmountByStoreIdAndPeriod(
                storeId, today.atStartOfDay(), today.plusDays(1).atStartOfDay());
        int yesterdayAmount = paymentRepository.sumAmountByStoreIdAndPeriod(
                storeId, yesterday.atStartOfDay(), today.atStartOfDay());

        int changePercent;
        if (yesterdayAmount == 0) {
            changePercent = todayAmount > 0 ? 999 : 0; // 999 = 전일 매출 없음 신호
        } else {
            changePercent = (int) Math.round((todayAmount - yesterdayAmount) / (double) yesterdayAmount * 100);
        }

        return SalesSummaryResponse.builder()
                .todayAmount(todayAmount)
                .yesterdayAmount(yesterdayAmount)
                .changePercent(changePercent)
                .build();
    }

    public List<TopProductResponse> getTopProducts(Long storeId) {
        List<Object[]> results = paymentRepository.findTopProducts(storeId);
        List<TopProductResponse> topList = new ArrayList<>();
        for (int i = 0; i < Math.min(5, results.size()); i++) {
            Object[] row = results.get(i);
            topList.add(TopProductResponse.builder()
                    .rank(i + 1)
                    .productName((String) row[0])
                    .totalCount(((Number) row[1]).intValue())
                    .build());
        }
        return topList;
    }

    private String maskEmail(String email) {
        // "hong@example.com" → "h**g"
        String name = email.split("@")[0];
        if (name.length() <= 2) return name;
        return name.charAt(0) + "**" + name.charAt(name.length() - 1);
    }
}
