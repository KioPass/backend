package com.mysite.sbb.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SellerUpgradeRequest {
	private String storename; // 상호명
    private String address;  // 매장 주소
}
