package com.mysite.sbb;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
	private int code;
	private String message;
	private T body;
	
	public static <T> ApiResponse<T> success(T body) {
		return new ApiResponse<>(200,"SUCCESS",body);
	}
	
	public static <T> ApiResponse<T> error(int code, String message) {
		return new ApiResponse<>(code,message,null);
	}
}
