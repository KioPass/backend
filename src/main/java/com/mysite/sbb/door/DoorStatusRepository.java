package com.mysite.sbb.door;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoorStatusRepository extends JpaRepository<DoorStatus, Long> {
    Optional<DoorStatus> findByStoreId(Long storeId);
}
