package com.mysite.sbb.inquiry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    List<Inquiry> findAllByOrderByCreatedAtDesc();
}
