package com.mysite.sbb.store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Store {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private String storename;

	private String address;

	private String licenseUrl;

	private String reportUrl;

	@Column(unique=true)
	private String userEmail;

	private double latitude;
	private double Longitude;

	private String status; // PENDING / APPROVED / REJECTED

	@CreationTimestamp
	private LocalDateTime createdAt;
}
