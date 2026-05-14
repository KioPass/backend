package com.mysite.sbb;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.mysite.sbb.user.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
	@Autowired
	private UserService userService;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	public String signup(String nickname, String email) {
		boolean isExist=userService.isExist(email);
		String location="";
		if(isExist) {//에러 : 이미 있는 회원
    		System.out.println("Error : 이미 있는 회원입니다.");
    		location="myapp://auth?status=error&code=409&message=already_exists";
    	} else {//새로 가입
    		System.out.println("회원가입 시작");
    		userService.create(nickname, email);
    		String token = jwtTokenProvider.createToken(email,"BUYER");//처음 기본 권한은 판매자(BUYER)
            location="myapp://auth?token=" + token + "&status=signup_success";
    	}
		return location;
	}
	
	public String login(String email) {
		boolean isExist=userService.isExist(email);
		String location="";
		if(isExist) {//회원 로그인
    		System.out.println("로그인 시작");
    		String role=userService.getRole(email);
    		String token = jwtTokenProvider.createToken(email,role);
    		System.out.println(token);
            location="myapp://auth?token=" + token + "&status=login_success";
    	} else {//에러 : 비회원
	    	System.out.println("Error : 비회원입니다.");
	    	location="myapp://auth?status=error&code=401&message=unauthorized";
    	}
		return location;
	}
	
	public String kakaoload(OAuth2User oAuth2User, String target) {
		Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) ((Map<String, Object>) oAuth2User.getAttribute("properties")).get("nickname");
        
        String location="";
        
        boolean isExist = userService.isExist(email);
	    if ("signup".equals(target)) {//회원가입
	    	if(isExist) {//에러 : 이미 있는 회원
	    		System.out.println("Error : 이미 있는 회원입니다.");
	    		location="myapp://auth?status=error&code=409&message=already_exists";
	    	} else {//새로 가입
	    		System.out.println("회원가입 시작");
	    		userService.create(nickname, email);
	    		String token = jwtTokenProvider.createToken(email,"BUYER");
                location="myapp://auth?token=" + token + "&status=signup_success";
	    	}
	    } else if("login".equals(target)) {//로그인
	    	if(isExist) {//회원 로그인
	    		System.out.println("로그인 시작");
	    		String role=userService.getRole(email);
	    		String token = jwtTokenProvider.createToken(email,role);
	    		System.out.println(token);
                location="myapp://auth?token=" + token + "&status=login_success";
	    	} else {//에러 : 비회원
		    	System.out.println("Error : 비회원입니다.");
		    	location="myapp://auth?status=error&code=401&message=unauthorized";
	    	}
	    } else {
	    	location="myapp://auth?status=error&code=404&message=unknown";
	    }
	    return location;
	}
	
	public String naverload(OAuth2User oAuth2User, String target) {
		Map<String, Object> attributes = oAuth2User.getAttributes();
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");
		String name = (String) response.get("name");
	    String email = (String) response.get("email");
	    
		String location="";
		
		if("signup".equals(target)) {
			location=this.signup(name,email);
		} else if("login".equals(target)) {
			location=this.login(email);
		} else {
			location="myapp://auth?status=error&code=404&message=unknown";
		}
		
		return location;
	}
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		OAuth2AuthenticationToken authToken=(OAuth2AuthenticationToken) authentication;
		String registrationId=authToken.getAuthorizedClientRegistrationId();
		
		HttpSession session = request.getSession();
        String target = (String) session.getAttribute("target");
        
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		
		if("kakao".equals(registrationId)) {//kakao
			String location=kakaoload(oAuth2User,target);
			response.sendRedirect(location);
		} else if("naver".equals(registrationId)) {//naver
			String location=naverload(oAuth2User,target);
			response.sendRedirect(location);
		}
	}
}
