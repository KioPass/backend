package com.mysite.sbb.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
	@GetMapping("/login")
	@ResponseBody
	public String login() {
		return "login_form";
	}
	
	@GetMapping("/auth/kakao")
	public String kakaoLogin(@RequestParam("target") String target, HttpServletRequest request) {
		request.getSession().setAttribute("target", target);
		return "redirect:/oauth2/authorization/kakao";
	}
	
	@GetMapping("/auth/naver")
	public String naverLogin(@RequestParam("target") String target, HttpServletRequest request) {
		request.getSession().setAttribute("target", target);
		return "redirect:/oauth2/authorization/naver";
	}
}
