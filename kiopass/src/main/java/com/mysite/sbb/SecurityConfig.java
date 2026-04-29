package com.mysite.sbb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.mysite.sbb.user.OAuth2UserService;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private OAuth2UserService oauth2UserService;
	@Autowired
	private CustomSuccessHandler customSuccessHandler;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		/*
		http
		.authorizeHttpRequests((authorizeHttpRequests)->authorizeHttpRequests
				.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
		.csrf((csrf)->csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
		.headers((headers)->headers.addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
		.oauth2Login((oauth2Login)->oauth2Login
				.loginPage("/user/login")
				.successHandler(customSuccessHandler)
				.userInfoEndpoint(userInfoEndpoint->userInfoEndpoint.userService(kakaoUserService))
				)
		;*/
		
		http
        .csrf(csrf -> csrf.disable())
        .headers(headers -> headers.frameOptions(frame -> frame.disable()))
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // ⭐ 세션 사용 안 함!
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/oauth2/**","/user/**", "/h2-console/**","/error/**").permitAll() // 로그인 관련은 모두 허용
            .requestMatchers("/api/**").authenticated()
            .anyRequest().authenticated()
        )
        .oauth2Login((oauth2Login)->oauth2Login
        		.loginPage("/user/login")
        		.successHandler(customSuccessHandler)
        		.userInfoEndpoint(userInfoEndpoint->userInfoEndpoint.userService(oauth2UserService))
        		)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(exception -> exception
        	    .authenticationEntryPoint((request, response, authException) -> {
        	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        	        response.setContentType("application/json");
        	        response.getWriter().write("{\"error\": \"인증에 실패했습니다. 토큰을 확인해주세요.\"}");
        	    })
        	)
        ;
		return http.build();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();

	    // 1. 허용할 Origin (테스트를 위해 google.com도 일단 허용)
	    configuration.addAllowedOrigin("https://www.google.com");
	    configuration.addAllowedOrigin("http://localhost:3000"); // 프론트엔드 주소
	    
	    // 2. 허용할 HTTP 메서드
	    configuration.addAllowedMethod("*"); 
	    
	    // 3. 허용할 헤더 (Authorization 헤더 필수!)
	    configuration.addAllowedHeader("*");
	    
	    // 4. 자격 증명(쿠키 등) 허용 여부
	    configuration.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}
}
