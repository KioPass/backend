package com.mysite.sbb.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.mysite.sbb.store.StoreService;
import com.mysite.sbb.store.StoreIdResponse;
import com.mysite.sbb.store.StoreRepository;
import com.mysite.sbb.file.FileService;
import com.mysite.sbb.notification.DeviceTokenRepository;
import com.mysite.sbb.payment.PaymentRepository;
import com.mysite.sbb.product.ProductRepository;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;
	private final StoreService storeService;
	private final StoreRepository storeRepository;
	private final FileService fileService;
	private final ProductRepository productRepository;
	private final PaymentRepository paymentRepository;
	private final DeviceTokenRepository deviceTokenRepository;
	
	public boolean isExist(String email) {
		Optional<SiteUser> _siteUser=userRepository.findByEmail(email);
		if(_siteUser.isEmpty()) {
			return false;
		}
		return true;
	}
	
	public String getRole(String email) {
		SiteUser siteUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));
		
		return siteUser.getRole();
	}
	
	public UserProfileResponse getUserProfile(String email) {
		SiteUser siteUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        return UserProfileResponse.builder()
                .email(siteUser.getEmail())
                .username(siteUser.getUsername())
                .role(siteUser.getRole())
                .build();
	}
	
	public StoreIdResponse upgradeToSeller(String email, SellerUpgradeRequest request, MultipartFile license, MultipartFile report) {
		SiteUser siteUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));
		
		String userEmail=siteUser.getEmail();
		Long userId=siteUser.getId();
		
		String storename=request.getStorename();
		String address=request.getAddress();
		
		String licenseUrl = fileService.upload(license,userId,userEmail);
        String reportUrl = fileService.upload(report,userId,userEmail);
		
		storeService.create(storename, address, licenseUrl, reportUrl, userEmail);

		// 역할은 관리자 승인 후 변경됨 (현재 PENDING 상태)
		return StoreIdResponse.builder()
				.storeId(0L)
				.build();
	}
	
	public SiteUser create(String username, String email) {
		SiteUser user=new SiteUser();
		user.setUsername(username);
		user.setEmail(email);
		user.setRole("BUYER");
		this.userRepository.save(user);
		return user;
	}

	public void promoteToSeller(String email) {
		SiteUser user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
		user.setRole("SELLER");
		userRepository.save(user);
	}

	public void promoteToAdmin(String email) {
		SiteUser user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
		user.setRole("ADMIN");
		userRepository.save(user);
	}

	public void deleteAccount(String email) {
		SiteUser user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

		// 판매자인 경우 매장 + 상품 + 매출 삭제
		storeRepository.findByUserEmail(email).ifPresent(store -> {
			productRepository.deleteAll(productRepository.findByStoreId(store.getId()));
			paymentRepository.deleteAll(paymentRepository.findByStoreIdOrderByCreatedAtDesc(store.getId()));
			storeRepository.delete(store);
		});

		// 구매자 결제 내역 삭제
		paymentRepository.deleteAll(paymentRepository.findByBuyerEmailOrderByCreatedAtDesc(email));

		// FCM 토큰 삭제
		deviceTokenRepository.findByEmail(email).ifPresent(deviceTokenRepository::delete);

		userRepository.delete(user);
	}
}
