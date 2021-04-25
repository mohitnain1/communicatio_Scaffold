package com.scaffold.web.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Response {
	
	public static final  <T> ResponseEntity<Object> generateResponse(HttpStatus status, T payload, 
			String message, boolean isSuccess) {
		Map<String, Object> responseData = new HashMap<String, Object>();
		responseData.put("data", payload);
		responseData.put("message", message);
		responseData.put("isSuccess", isSuccess);
		return new ResponseEntity<Object>(responseData, status);
	}
}
