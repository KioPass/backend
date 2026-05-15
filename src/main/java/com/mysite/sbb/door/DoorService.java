package com.mysite.sbb.door;

import com.mysite.sbb.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoorService {

    private final DoorEntryRepository doorEntryRepository;
    private final DoorStatusRepository doorStatusRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean verify(String userEmail, Long storeId, String storeName) {
        boolean userExists = userRepository.findByEmail(userEmail).isPresent();

        DoorEntry entry = new DoorEntry();
        entry.setUserEmail(userEmail);
        entry.setStoreId(storeId);
        entry.setStoreName(storeName);
        entry.setResult(userExists ? "SUCCESS" : "DENIED");
        doorEntryRepository.save(entry);

        if (userExists) {
            // 도어 열림 상태 저장 (ESP32가 폴링)
            DoorStatus status = doorStatusRepository.findByStoreId(storeId)
                    .orElseGet(() -> {
                        DoorStatus s = new DoorStatus();
                        s.setStoreId(storeId);
                        return s;
                    });
            status.setOpen(true);
            status.setOpenedAt(System.currentTimeMillis());
            doorStatusRepository.save(status);
        }

        return userExists;
    }

    // ESP32 폴링용 - 3초 지나면 자동으로 false 반환
    public boolean isOpen(Long storeId) {
        return doorStatusRepository.findByStoreId(storeId)
                .map(s -> {
                    if (!s.isOpen()) return false;
                    boolean expired = System.currentTimeMillis() - s.getOpenedAt() > 3000;
                    if (expired) {
                        s.setOpen(false);
                        doorStatusRepository.save(s);
                        return false;
                    }
                    return true;
                })
                .orElse(false);
    }
}
