package com.mysite.sbb.door;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "storeId"))
public class DoorStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long storeId;
    private boolean open;          // ESP32가 폴링해서 확인
    private long openedAt;         // Unix timestamp (밀리초) — 3초 후 자동 닫힘
}
