package com.mysite.sbb.store;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.sbb.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {
	private final StoreService storeService;
	
	@GetMapping("/my")
	public ResponseEntity<ApiResponse<StoreIdResponse>> getMyStore(
			@AuthenticationPrincipal UserDetails userDetails) {
		try {
			Store store = storeService.getStore(userDetails.getUsername());
			return ResponseEntity.ok(ApiResponse.success(
				StoreIdResponse.builder()
					.storeId(store.getId())
					.storeName(store.getStorename())
					.status(store.getStatus())
					.build()));
		} catch (RuntimeException e) {
			return ResponseEntity.ok(ApiResponse.success(null));
		}
	}

	@GetMapping("/nearby")
	public ResponseEntity<ApiResponse<List<NearbyStoreResponse>>> getNearbyStore(
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(value="latitude") Double latitude,
            @RequestParam(value="longitude") Double longitude
            ) {
		List<NearbyStoreResponse> nearstore=this.storeService.getNearby(latitude, longitude);
		
		return ResponseEntity.ok(ApiResponse.success(nearstore));
	}
}
