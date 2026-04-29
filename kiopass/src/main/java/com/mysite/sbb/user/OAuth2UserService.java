package com.mysite.sbb.user;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
	
	private final UserRepository userRepository;
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User=super.loadUser(userRequest);
		String registrationId=userRequest.getClientRegistration().getRegistrationId();
		
		if("kakao".equals(registrationId)) {
			System.out.println("카카오");
		} else if("naver".equals(registrationId)) {
			Map<String, Object> attributes=oAuth2User.getAttributes();
			System.out.println("네이버"+attributes);
		}
		/*
		Map<String, Object> attributes=oAuth2User.getAttributes();
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		
		String email=(String) kakaoAccount.get("email");
		Optional<SiteUser> _siteUser=userRepository.findByEmail(email);
		if(_siteUser.isEmpty()) {
			System.out.println("ka비회원!");
			//TODO 비회원 에러 처리
			//throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
		}
		System.out.println("카카오"+attributes);
		*/
		return oAuth2User;
	}
}
