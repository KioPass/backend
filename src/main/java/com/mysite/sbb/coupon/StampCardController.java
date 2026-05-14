package com.mysite.sbb.coupon;

import com.mysite.sbb.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stamps")
@RequiredArgsConstructor
public class StampCardController {

    private final StampCardService stampCardService;

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<StampCardResponse>>> getMyCoupons(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                stampCardService.getMyCoupons(userDetails.getUsername())));
    }

    @GetMapping("/my/{storeId}")
    public ResponseEntity<ApiResponse<StampCardResponse>> getStampCard(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                stampCardService.getStampCard(userDetails.getUsername(), storeId)));
    }

    @PostMapping("/use/{storeId}")
    public ResponseEntity<ApiResponse<StampCardResponse>> useCoupon(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                stampCardService.useCoupon(userDetails.getUsername(), storeId)));
    }
}
