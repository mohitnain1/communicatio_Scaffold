package com.scaffold.chat.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.scaffold.chat.service.AWSS3Service;
@Service
public class AWSS3ServiceImpl implements AWSS3Service{
	private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3ServiceImpl.class);

	@Autowired private AmazonS3 amazonS3;
	
	@Value("${aws.s3.bucket}")
	private String bucketName;
	private String uniqueFileName=null;
	
	//Here Upload file on AWS S3 bucket......
	@Override
	@Async
	public String uploadFile(MultipartFile multipartFile) {
		try {
			LOGGER.info("File upload in progress...");
			File file = convertMultiPartFileToFile(multipartFile);
			uniqueFileName = System.currentTimeMillis() +"-"+UUID.randomUUID().toString().substring(0, 3)+ "-" + file.getName();
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
			amazonS3.putObject(putObjectRequest);
			LOGGER.info("File upload is completed.");
			file.delete();
		} catch (AmazonServiceException ex) {
			LOGGER.error("Error= {} while uploading file.", ex.getMessage());
		}
		return uniqueFileName;
	}
	
	//Here Convert multipartFile into File..............
	private File convertMultiPartFileToFile(MultipartFile multipartFile) {
		File file = new File(multipartFile.getOriginalFilename());
		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			outputStream.write(multipartFile.getBytes());
		} catch (IOException ex) {
			LOGGER.error("Error converting the multi-part file to file= ", ex.getMessage());
		}
		return file;
	}
	
	//Here Download file from s3 bucket....
	public byte[] downloadFile(String fileName) {
		S3Object s3Object = amazonS3.getObject(bucketName, fileName);
		S3ObjectInputStream inputStream = s3Object.getObjectContent();
		try {
			byte[] content = IOUtils.toByteArray(inputStream);
			LOGGER.info("File download successfully...");
			return content;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//Here Delete file from s3 bucket....
	public String deleteFile(String fileName) {
		amazonS3.deleteObject(bucketName, fileName);
		return fileName+" deleted successfully.";
	}
}
