package com.mysite.sbb.notification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceTokenRequest {
    private String token;
    private String platform;
}
