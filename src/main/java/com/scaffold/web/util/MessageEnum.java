package com.scaffold.web.util;

public enum MessageEnum {
	IMAGE("Image"), TEXT("Text");
	
	private String value;
	
	MessageEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
