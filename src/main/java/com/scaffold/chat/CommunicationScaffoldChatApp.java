package com.scaffold.chat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.scaffold.security.jwt.JwtUtil;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ConfigurationPropertiesScan(basePackages = "com.scaffold.*.*")
public class CommunicationScaffoldChatApp {

	public static void main(String[] args) {

		SpringApplication.run(CommunicationScaffoldChatApp.class, args);
	}

	public static final Contact DEFAULT_CONTACT = new Contact("Communication Scaffold", "http://oodlestechnologies.com",
			"shubhmoykumar.garg@oodlestechnologies.com");

	public static final ApiInfo DEFAULT_API_INFO = new ApiInfo("Communication Scaffold service API",
			"The API provides a platform to query build Communication Scaffold api", "1.0", "urn:tos", DEFAULT_CONTACT,
			"Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", Arrays.asList());

	private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<String>(
			Arrays.asList("application/json"));

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(DEFAULT_API_INFO).produces(DEFAULT_PRODUCES_AND_CONSUMES)
				.consumes(DEFAULT_PRODUCES_AND_CONSUMES);
	}

	@Bean
	public JwtUtil jwtUtil() {
		return new JwtUtil();
	}
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
	    return new WebMvcConfigurer () {
	        @Override
	        public void addCorsMappings(CorsRegistry registry) {
	            registry.addMapping("/**")
	            	.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
	            	.allowCredentials(true)
	            	.allowedOriginPatterns("*");
	        }
	    };
	}
	
	@Bean
	public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
	    StrictHttpFirewall firewall = new StrictHttpFirewall();
	    firewall.setAllowUrlEncodedSlash(true);    
	    return firewall;
	}
}
