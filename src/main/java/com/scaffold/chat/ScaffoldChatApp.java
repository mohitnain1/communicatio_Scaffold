package com.scaffold.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EnableSwagger2
public class ScaffoldChatApp {

	public static void main(String[] args) {
		SpringApplication.run(ScaffoldChatApp.class, args);
	}
	
	@Bean
	public Docket swaggerConfiguration() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo());
	}
	
	public ApiInfo apiInfo() {
		final ApiInfoBuilder builder = new ApiInfoBuilder();
		builder.title("Communication Scaffold service API").version("1.0")
				.license("(C) Copyright Communication Scaffold")
				.description("The API provides a platform to query build Communication Scaffold api")
				.contact(new Contact("Communication Scaffold", "http://oodlestechnologies.com",
						"shubhmoykumar.garg@oodlestechnologies.com"));
		return builder.build();
	}
}
