package com.scaffold.web.util;

public enum MessageEnum {
	FILE("FILE"), TEXT("TEXT"), UPDATE_MEMBER("UPDATE_MEMBER"), REMOVE("REMOVE"),
	ADD("ADD"), CALL_INITIATED("CALL_INITIATED"), CALL_ACCEPTED("CALL_ACCEPTED"), 
	CALL_REJECTED("CALL_REJECTED"), INCOMING_CALL("INCOMING_CALL");
	
	private String value;
	
	MessageEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
