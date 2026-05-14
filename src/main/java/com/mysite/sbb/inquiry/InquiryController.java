package com.mysite.sbb.inquiry;

import com.mysite.sbb.ApiResponse;
import com.mysite.sbb.notification.NotificationService;
import com.mysite.sbb.store.SellerApplicationResponse;
import com.mysite.sbb.store.Store;
import com.mysite.sbb.store.StoreRepository;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;
import com.mysite.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;
    private final UserService userService;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Value("${admin.secret}")
    private String adminSecret;

    @PostMapping("/api/inquiries")
    public ResponseEntity<ApiResponse<InquiryResponse>> createInquiry(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody InquiryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                inquiryService.createInquiry(
                        userDetails.getUsername(), request.getCategory(), request.getContent())));
    }

    @GetMapping("/api/inquiries/my")
    public ResponseEntity<ApiResponse<List<InquiryResponse>>> getMyInquiries(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                inquiryService.getMyInquiries(userDetails.getUsername())));
    }

    @GetMapping("/api/admin/inquiries")
    public ResponseEntity<ApiResponse<List<InquiryResponse>>> getAllInquiries() {
        return ResponseEntity.ok(ApiResponse.success(inquiryService.getAllInquiries()));
    }

    @DeleteMapping("/api/inquiries/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMyInquiry(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        inquiryService.deleteInquiry(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("삭제 완료"));
    }

    @DeleteMapping("/api/admin/inquiries/{id}")
    public ResponseEntity<ApiResponse<String>> adminDeleteInquiry(@PathVariable Long id) {
        inquiryService.adminDeleteInquiry(id);
        return ResponseEntity.ok(ApiResponse.success("삭제 완료"));
    }

    @PostMapping("/api/admin/inquiries/{id}/answer")
    public ResponseEntity<ApiResponse<InquiryResponse>> answerInquiry(
            @PathVariable Long id,
            @RequestBody InquiryAnswerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                inquiryService.answerInquiry(id, request.getAnswer())));
    }

    // ── 판매자 신청 관리 ──────────────────────────────────────────────────
    @GetMapping("/api/admin/seller-applications")
    public ResponseEntity<ApiResponse<List<SellerApplicationResponse>>> getSellerApplications() {
        List<SellerApplicationResponse> list = storeRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(store -> {
                    String userName = userRepository.findByEmail(store.getUserEmail())
                            .map(SiteUser::getUsername).orElse("");
                    return SellerApplicationResponse.from(store, userName);
                }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/api/admin/seller-applications/{storeId}/approve")
    public ResponseEntity<ApiResponse<String>> approveSeller(@PathVariable Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다."));
        store.setStatus("APPROVED");
        storeRepository.save(store);
        userService.promoteToSeller(store.getUserEmail());
        notificationService.send(store.getUserEmail(), "판매자 신청이 승인됐어요 🎉", store.getStorename() + " 판매자로 전환됐어요. 재로그인 후 이용해주세요.");
        return ResponseEntity.ok(ApiResponse.success("승인 완료"));
    }

    @PostMapping("/api/admin/seller-applications/{storeId}/reject")
    public ResponseEntity<ApiResponse<String>> rejectSeller(@PathVariable Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다."));
        store.setStatus("REJECTED");
        storeRepository.save(store);
        notificationService.send(store.getUserEmail(), "판매자 신청 결과 안내", store.getStorename() + " 신청이 거절됐어요. 고객센터로 문의해주세요.");
        return ResponseEntity.ok(ApiResponse.success("거절 완료"));
    }

    @PostMapping("/api/admin/setup")
    public ResponseEntity<ApiResponse<String>> setupAdmin(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String secret = body.get("secret");
        if (!adminSecret.equals(secret)) {
            return ResponseEntity.status(403).body(ApiResponse.error(403, "잘못된 시크릿입니다."));
        }
        userService.promoteToAdmin(email);
        return ResponseEntity.ok(ApiResponse.success("관리자 권한이 부여됐습니다."));
    }
}
