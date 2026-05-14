package com.mysite.sbb.notification;

import com.mysite.sbb.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class DeviceTokenController {

    private final NotificationService notificationService;

    @PostMapping("/device-token")
    public ResponseEntity<ApiResponse<Void>> saveToken(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DeviceTokenRequest request) {
        notificationService.saveToken(
                userDetails.getUsername(),
                request.getToken(),
                request.getPlatform());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
