package com.scaffold.web.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "com.scaffold")
public @Data class  ScaffoldProperties {

	private String brokerUsername;
	private String brokerPassword;
	private String brokerHostName;
	private int brokerPort;
	
	private String adminUsername;
	private String adminPassword;
	private String adminEmail;

}
