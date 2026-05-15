package com.mysite.sbb.door;

import com.mysite.sbb.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/door")
@RequiredArgsConstructor
public class DoorController {

    private final DoorService doorService;

    // 앱에서 QR 스캔 후 호출
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verify(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long storeId = Long.valueOf(body.get("storeId").toString());
        String storeName = (String) body.getOrDefault("storeName", "");

        boolean allowed = doorService.verify(userDetails.getUsername(), storeId, storeName);

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "open", allowed,
                "message", allowed ? "입장이 허가됐습니다" : "등록되지 않은 사용자입니다"
        )));
    }

    // ESP32가 폴링하는 엔드포인트 (인증 불필요)
    @GetMapping("/status/{storeId}")
    public ResponseEntity<Map<String, Object>> status(@PathVariable Long storeId) {
        boolean open = doorService.isOpen(storeId);
        return ResponseEntity.ok(Map.of("open", open));
    }
}
