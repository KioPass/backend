package com.mysite.sbb.door;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class DoorEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private Long storeId;
    private String storeName;

    private String result; // SUCCESS / DENIED

    @CreationTimestamp
    private LocalDateTime enteredAt;
}
