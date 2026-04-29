package com.mysite.sbb.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NearbyStoreResponse {
	private Long id;
	private String storename;
	private String address;
}
