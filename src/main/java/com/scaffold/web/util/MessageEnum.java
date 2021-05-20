package com.scaffold.web.util;

public enum MessageEnum {
	IMAGE("IMAGE"), TEXT("TEXT"), UPDATE_MEMBER("UPDATE_MEMBER");
	
	private String value;
	
	MessageEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
