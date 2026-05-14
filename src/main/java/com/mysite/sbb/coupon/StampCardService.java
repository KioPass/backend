package com.mysite.sbb.coupon;

import com.mysite.sbb.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StampCardService {

    private static final int STAMPS_PER_COUPON = 10;

    private final StampCardRepository stampCardRepository;
    private final NotificationService notificationService;

    @Transactional
    public StampCardResponse addStamp(String buyerEmail, Long storeId, String storeName) {
        if (buyerEmail == null || storeId == null || storeId == 0) return null;

        StampCard card = stampCardRepository
                .findByBuyerEmailAndStoreId(buyerEmail, storeId)
                .orElseGet(() -> {
                    StampCard c = new StampCard();
                    c.setBuyerEmail(buyerEmail);
                    c.setStoreId(storeId);
                    c.setStoreName(storeName);
                    return c;
                });

        card.setStampCount(card.getStampCount() + 1);

        if (card.getStampCount() % STAMPS_PER_COUPON == 0) {
            card.setAvailableCoupons(card.getAvailableCoupons() + 1);
            card.setTotalEarned(card.getTotalEarned() + 1);
            notificationService.send(buyerEmail,
                    "쿠폰이 발급됐어요 🎟️",
                    storeName + "에서 사용 가능한 1,000원 쿠폰이 생겼어요!");
        }

        return StampCardResponse.from(stampCardRepository.save(card));
    }

    public List<StampCardResponse> getMyCoupons(String buyerEmail) {
        return stampCardRepository.findByBuyerEmailOrderByStoreNameAsc(buyerEmail)
                .stream()
                .filter(c -> c.getStampCount() > 0 || c.getAvailableCoupons() > 0)
                .map(StampCardResponse::from)
                .collect(Collectors.toList());
    }

    public StampCardResponse getStampCard(String buyerEmail, Long storeId) {
        return stampCardRepository.findByBuyerEmailAndStoreId(buyerEmail, storeId)
                .map(StampCardResponse::from)
                .orElse(StampCardResponse.builder()
                        .storeId(storeId)
                        .stampCount(0)
                        .availableCoupons(0)
                        .totalEarned(0)
                        .build());
    }

    @Transactional
    public StampCardResponse useCoupon(String buyerEmail, Long storeId) {
        StampCard card = stampCardRepository.findByBuyerEmailAndStoreId(buyerEmail, storeId)
                .orElseThrow(() -> new RuntimeException("스탬프 카드가 없습니다."));
        if (card.getAvailableCoupons() <= 0) {
            throw new RuntimeException("사용 가능한 쿠폰이 없습니다.");
        }
        card.setAvailableCoupons(card.getAvailableCoupons() - 1);
        return StampCardResponse.from(stampCardRepository.save(card));
    }
}
