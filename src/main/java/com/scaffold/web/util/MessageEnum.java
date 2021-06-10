package com.scaffold.web.util;

public enum MessageEnum {
	FILE("FILE"), TEXT("TEXT"), UPDATE_MEMBER("UPDATE_MEMBER"), START_CALL("VIDEO_CALL"), REMOVE("REMOVE"), ADD("ADD");
	
	private String value;
	
	MessageEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
