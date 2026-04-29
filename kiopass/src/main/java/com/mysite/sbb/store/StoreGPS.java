package com.mysite.sbb.store;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreGPS {
	private List<Document> documents;
	
	@Getter
	@Setter
	public static class Document {
		private String x;
		private String y;
		private String address_name;
	}
}
