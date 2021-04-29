package com.scaffold.web.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.IdGenerator;

public class SimpleIdGenerator implements IdGenerator {

	private final AtomicInteger atomicInteger = new AtomicInteger();	
	@Override
	public UUID generateId() {
		return UUID.randomUUID();
	}
	
	public String generateRandomId() {
		return UUID.randomUUID().toString().substring(0, 16).replace("-", "")
				.concat(String.valueOf(atomicInteger.getAndIncrement()));
	}

}
