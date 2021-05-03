package com.scaffold.chat.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
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
import com.scaffold.chat.model.ChatPayload;
import com.scaffold.chat.model.Message;
import com.scaffold.chat.model.User;
import com.scaffold.chat.repository.UsersDetailRepository;
import com.scaffold.chat.service.ChatFileUploadService;
import com.scaffold.chat.ws.event.MessageEventHandler;
import com.scaffold.security.domains.UserCredentials;
import com.scaffold.web.util.MessageEnum;

@Service
public class ChatFileUploadServiceImpl implements ChatFileUploadService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChatFileUploadServiceImpl.class);

	@Autowired
	private AmazonS3 amazonS3;

	@Value("${cloud.aws.bucket.name}")
	private String bucketName;
	private String uniqueFileName = null;
	
	@Autowired MessageEventHandler messageEventHandler; 
	@Autowired UsersDetailRepository userDetailsRepository;

	@Override
	@Async
	public Map<String, Object> uploadFile(FileUploadParms fileParms, HttpServletRequest request) {
		try {
			String file = fileParms.getFileData().split(",")[1];
			byte[] fileData = java.util.Base64.getDecoder().decode(file.getBytes());
			InputStream data = new ByteArrayInputStream(fileData);
			
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(fileData.length);
			
			uniqueFileName = System.currentTimeMillis() + "-" + 
			UUID.randomUUID().toString().substring(0, 3) + "-"+ fileParms.getFileName();
			
			new Thread(() -> {amazonS3.putObject(bucketName, uniqueFileName, data, metadata);}).start();
			LOGGER.info("File uploading successfully...");
		} catch (AmazonServiceException ex) {
			LOGGER.error("Error= {} while uploading file.", ex.getMessage());
		}
		return saveMessageAndReturnContent(generateDownloadLink(request, uniqueFileName), fileParms);
	}

	private Map<String, Object> saveMessageAndReturnContent(String generateDownloadLink, FileUploadParms fileParms) {
		String destination = "/app/chat." + fileParms.getChatRoomId();
		ChatPayload payload = new ChatPayload(fileParms.getSenderId(), generateDownloadLink, destination);
		payload.setContentType(MessageEnum.IMAGE.getValue());
		Message savedMessage = messageEventHandler.saveFileUploadParam(payload);
		User user = userDetailsRepository.findByUserId(fileParms.getSenderId());
		UserCredentials sender = new UserCredentials(user.getUserId(), user.getUserProfilePicture(), user.getUsername());
		messageEventHandler.newMessageEvent(savedMessage, sender);
		return messageEventHandler.getResponseForClient(sender, savedMessage);
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
