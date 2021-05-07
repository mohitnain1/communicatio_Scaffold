package com.scaffold.web.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "com.scaffold")
public @Data class  ScaffoldProperties {

	private String brokerUsername;
	private String brokerPassword;
	private String brokerHostName;
	private int brokerPort;

}
