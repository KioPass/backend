package com.mysite.sbb.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final DeviceTokenRepository deviceTokenRepository;

    public void saveToken(String email, String token, String platform) {
        DeviceToken dt = deviceTokenRepository.findByEmail(email)
                .orElse(new DeviceToken());
        dt.setEmail(email);
        dt.setToken(token);
        dt.setPlatform(platform);
        deviceTokenRepository.save(dt);
    }

    public void send(String email, String title, String body) {
        Optional<DeviceToken> opt = deviceTokenRepository.findByEmail(email);
        if (opt.isEmpty()) return;

        String token = opt.get().getToken();
        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setToken(token)
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 발송 성공: {}", response);
        } catch (Exception e) {
            log.error("FCM 발송 실패 [{}]: {}", email, e.getMessage());
        }
    }
}
