package com.mysite.sbb.store;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StoreService {
	
	private final StoreRepository storeRepository;
	
	public Store create(String storename, String address, String licenseUrl, String reportUrl, String userEmail) {
		Store store=new Store();
		store.setStorename(storename);
		store.setAddress(address);
		store.setLicenseUrl(licenseUrl);
		store.setReportUrl(reportUrl);
		store.setUserEmail(userEmail);
		
		StoreGPS.Document storeGPSdocu=getLocation(address);
		
		System.out.println(storeGPSdocu.getX()+", "+storeGPSdocu.getY());
		
		store.setLatitude(Double.parseDouble(storeGPSdocu.getY()));
		store.setLongitude(Double.parseDouble(storeGPSdocu.getX()));
		
		this.storeRepository.save(store);
		
		return store;
	}
	
	public Long getId(String email) {
		Store store=this.storeRepository.findByUserEmail(email)
				.orElseThrow(() -> new RuntimeException("해당 매장을 찾을 수 없습니다."));
		
		return store.getId();
	}
	
	public StoreGPS.Document getLocation(String address) {
		//TODO 꼼수로 다른 api 호출함;;;
		String restApiKey = "99e0c5852f53baf38b50d9c4773dc50c"; 
        String apiURL = "https://dapi.kakao.com/v2/local/search/address.json";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + restApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        RestTemplate restTemplate=new RestTemplate();
        
        URI uri = UriComponentsBuilder
                .fromHttpUrl(apiURL)
                .queryParam("query", address)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
        
        ResponseEntity<StoreGPS> response=restTemplate.exchange(
        		uri,
        		HttpMethod.GET,
        		entity,
        		StoreGPS.class
        		);
        
        if (response.getBody() != null && !response.getBody().getDocuments().isEmpty()) {
            return response.getBody().getDocuments().get(0);
        }
        return null;
	}
	
	public List<NearbyStoreResponse> getNearby(Double latitude, Double longitude) {
		List<Store> nearStore=this.storeRepository.findNearbyStores(latitude, longitude, 5.0);
		//System.out.println(nearStore.get(0).getLatitude());
		//System.out.println(nearStore.get(0).getLongitude());
		if(nearStore.isEmpty()) {
			return Collections.emptyList();
		}
		List<NearbyStoreResponse> storelist=new ArrayList<>();
		for(Store item : nearStore) {
			storelist.add(
					NearbyStoreResponse.builder()
					.id(item.getId())
					.storename(item.getStorename())
					.address(item.getAddress())
					.build());
		}
		return storelist;
	}
	
	
}
