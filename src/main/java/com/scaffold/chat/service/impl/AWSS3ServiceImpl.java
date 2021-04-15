package com.scaffold.chat.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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
	//@Autowired private Environment env;
	
	@Value("${cloud.aws.bucket.name}")
	private String bucketName;
	private String uniqueFileName=null;
	
	//Here Upload file on AWS S3 bucket......
	@Override
	@Async
	public String uploadFile(MultipartFile multipartFile, HttpServletRequest request) {
		try {
			//String property = System.getenv("AWS_ACCESS_KEY_ID");
			//String property2 = System.getenv("AWS_SECRET_ACCESS_KEY");
			//System.out.println("env"+property2+"  "+property);
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
		return generateDownloadLink(request, uniqueFileName);
	}
	
//	private String generateDownloadLink(HttpServletRequest request, String fileName) {
//		RedirectUrlBuilder download_Uri = new RedirectUrlBuilder ();
//		int serverPort = request.getServerPort();
//		download_Uri.setScheme (request.getScheme ());
//		download_Uri.setContextPath (request.getLocalName());
//		download_Uri.setPort (serverPort);
//		download_Uri.setPathInfo ("chat/downloadFile/" + fileName);
//		download_Uri.setServerName (request.getServerName ());
//		return download_Uri.getUrl();
//	}
	
	//Here generate file download Link.....
	private String generateDownloadLink(HttpServletRequest request, String fileName) {
		UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(request.getScheme())
				.host(request.getLocalName()).port(request.getServerPort()).path("chat/downloadFile/"+fileName).build();
		return uriComponents.toUriString();
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
