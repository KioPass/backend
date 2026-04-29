package com.mysite.sbb.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.mysite.sbb.store.StoreService;
import com.mysite.sbb.store.StoreIdResponse;
import com.mysite.sbb.file.FileService;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;
	private final StoreService storeService;
	private final FileService fileService;
	
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
		
		long storeId=storeService.getId(userEmail);
		
		siteUser.setRole("SELLER");
		this.userRepository.save(siteUser);
		
		return StoreIdResponse.builder()
				.storeId(storeId)
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
}
