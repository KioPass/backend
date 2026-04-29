package com.mysite.sbb.store;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreRepository extends JpaRepository<Store, Long> {
	@Query(value = "SELECT *, " +
	           "(6371 * acos(cos(radians(:lat)) * cos(radians(s.latitude)) * " +
	           "cos(radians(s.longitude) - radians(:lon)) + " +
	           "sin(radians(:lat)) * sin(radians(s.latitude)))) AS distance " +
	           "FROM store s " +
	           "HAVING distance <= :radius " +
	           "ORDER BY distance", nativeQuery = true)
	List<Store> findNearbyStores(@Param("lat") Double lat, 
	                                 @Param("lon") Double lon, 
	                                 @Param("radius") Double radius);
	
	Optional<Store> findByUserEmail(String email);
}
