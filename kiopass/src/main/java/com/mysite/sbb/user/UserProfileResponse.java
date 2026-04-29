package com.mysite.sbb.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserProfileResponse {
	private String email;
    private String username;
    private String role;
}
