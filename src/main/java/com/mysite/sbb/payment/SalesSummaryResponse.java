package com.mysite.sbb.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SalesSummaryResponse {
    private int todayAmount;
    private int yesterdayAmount;
    private int changePercent; // 전일 대비 % (양수 = 상승, 음수 = 하락)
}
