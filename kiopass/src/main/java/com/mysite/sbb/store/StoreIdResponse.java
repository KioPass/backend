package com.mysite.sbb.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StoreIdResponse {
	private long storeId;
}
