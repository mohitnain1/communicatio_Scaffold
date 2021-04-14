package com.scaffold.chat.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AWSS3Configuration {
	@Value("${aws.access_key_id}")
	//@Value("${cloud.aws.credentials.access-key}")
	private String accessKeyId;
	@Value("${aws.secret_access_key}")
	//@Value("${cloud.aws.credentials.secret-key}")
	private String secretAccessKey;
	@Value("${aws.s3.region}")
	//@Value("${cloud.aws.region.static}")
	private String region;
	
	@Bean
	public AmazonS3 getAmazonS3Cient() {
		final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
		return AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region)).withCredentials
				(new AWSStaticCredentialsProvider(basicAWSCredentials)).build();
	}

}
