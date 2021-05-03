package com.scaffold.chat.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.scaffold.chat.datatransfer.FileUploadParms;
import com.scaffold.chat.service.ChatFileUploadService;

@Service
public class ChatFileUploadServiceImpl implements ChatFileUploadService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChatFileUploadServiceImpl.class);

	@Autowired
	private AmazonS3 amazonS3;

	@Value("${cloud.aws.bucket.name}")
	private String bucketName;
	private String uniqueFileName = null;

	@Override
	@Async
	public String uploadFile(FileUploadParms fileParms, HttpServletRequest request) {
		try {
			String file = fileParms.getFileData();
			byte[] fileData = java.util.Base64.getDecoder().decode(file.getBytes());
			InputStream data = new ByteArrayInputStream(fileData);
			
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(fileData.length);
			
			uniqueFileName = System.currentTimeMillis() + "-" + 
			UUID.randomUUID().toString().substring(0, 3) + "-"+ fileParms.getFileName();
			
			amazonS3.putObject(bucketName, uniqueFileName, data, metadata);
			LOGGER.info("File uploading successfully...");
		} catch (AmazonServiceException ex) {
			LOGGER.error("Error= {} while uploading file.", ex.getMessage());
		}
		return generateDownloadLink(request, uniqueFileName);
	}

	private String generateDownloadLink(HttpServletRequest request, String fileName) {
		UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(request.getScheme())
				.host(request.getLocalName()).port(request.getServerPort()).path("chat/download/" + fileName).build();
		return uriComponents.toUriString();
	}

	// Here Download file from s3 bucket....
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

	// Here Delete file from s3 bucket....
	public String deleteFile(String fileName) {
		amazonS3.deleteObject(bucketName, fileName);
		return fileName + " deleted successfully.";
	}

}
