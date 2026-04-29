package com.mysite.sbb.user;

import com.mysite.sbb.ApiResponse;
import com.mysite.sbb.store.StoreIdResponse;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserAPIController {
	
	private final UserService userService;
	
	//private final FileService fileService;
	
	@GetMapping("/profile")
	public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // userDetails에는 JwtFilter에서 채워넣은 유저의 이메일 정보가 들어있습니다.
        String email = userDetails.getUsername();
        
        UserProfileResponse profile=userService.getUserProfile(email);
        
        //return ResponseEntity.ok(profile);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
	
	@PostMapping(value = "/upgrade", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<StoreIdResponse>> upgradeToSeller (
		@AuthenticationPrincipal UserDetails userDetails,
	    @RequestPart("info") SellerUpgradeRequest request, // 상호명, 주소 등 JSON
	    @RequestPart("businessLicense") MultipartFile businessLicense, // 사업자등록증 사진
	    @RequestPart("operationReport") MultipartFile operationReport   // 영업신고증 사진
	) {
		String email = userDetails.getUsername();
        StoreIdResponse storeId=userService.upgradeToSeller(email, request, businessLicense, operationReport);
        
	    return ResponseEntity.ok(ApiResponse.success(storeId));
	}
}
