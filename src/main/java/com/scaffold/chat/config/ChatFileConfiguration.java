package com.scaffold.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class ChatFileConfiguration {
	
	private String accessKeyId;
	
	private String secretAccessKey;
	
	@Value("${cloud.aws.region.static}")
	private String region;
	
	@Bean
	public AmazonS3 getAmazonS3Cient() {
		secretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY");
		accessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
		final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
		return AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region)).withCredentials
				(new AWSStaticCredentialsProvider(basicAWSCredentials)).build();
	}

}
