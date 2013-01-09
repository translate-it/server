package com.amadeus.ori.translate.domain;

public class Format {

	private String id;
	private String description;
	
	public Format(String id, String description) {
		super();
		this.id = id;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
}
