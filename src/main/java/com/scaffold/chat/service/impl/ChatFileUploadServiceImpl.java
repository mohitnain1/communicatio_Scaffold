package com.scaffold.chat.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
import com.scaffold.web.util.FileData;
import com.scaffold.web.util.MessageEnum;

@Service
public class ChatFileUploadServiceImpl implements ChatFileUploadService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChatFileUploadServiceImpl.class);
	private final ExecutorService exec = Executors.newFixedThreadPool(5);
	
	@Autowired
	private AmazonS3 amazonS3;
	
	@Autowired SimpMessagingTemplate simpMessagingTemplate;

	@Value("${cloud.aws.bucket.name}")
	private String bucketName;
	private String uniqueFileName = null;
	
	@Autowired MessageEventHandler messageEventHandler; 
	@Autowired UsersDetailRepository userDetailsRepository;

	@Override
	@Async
	public List<Map<String, Object>> uploadFile(FileUploadParms fileParms, HttpServletRequest request) {
		try {
			List<FileData> files = fileParms.getFiles();
			List<String> downloadsLinks = new ArrayList<>();
			for (FileData file : files) {
				String base64Data = file.getFileData().split(",")[1];
				byte[] fileData = Base64.getDecoder().decode(base64Data.getBytes());
				InputStream data = new ByteArrayInputStream(fileData);

				ObjectMetadata metadata = new ObjectMetadata();
				metadata.setContentLength(fileData.length);

				uniqueFileName = System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 3) + "-"+ file.getFileName();
				downloadsLinks.add(generateDownloadLink(request, uniqueFileName));
				exec.submit(() -> {
					LOGGER.info("Uploading {} ", uniqueFileName);
					amazonS3.putObject(bucketName, uniqueFileName, data, metadata);
					LOGGER.info("Finished uploading file {}", uniqueFileName);
				});
			}
			return saveMessageAndReturnContent(downloadsLinks, fileParms);
		} catch (AmazonServiceException ex) {
			LOGGER.error("Error= {} while uploading file.", ex.getMessage());
			return Collections.emptyList();
		}
	}

	private List<Map<String, Object>> saveMessageAndReturnContent(List<String> downloadsLinks, FileUploadParms fileParms) {
		List<Map<String, Object>> response = new ArrayList<>();
		User user = userDetailsRepository.findByUserId(fileParms.getSenderId());
		UserCredentials sender = new UserCredentials(user.getUserId(), user.getUserProfilePicture(), user.getUsername());
		String destination = "/app/chat." + fileParms.getChatRoomId();
		for(String link  : downloadsLinks) {
			ChatPayload payload = new ChatPayload(fileParms.getSenderId(), link, destination);
			payload.setContentType(MessageEnum.IMAGE.getValue());
			Message savedMessage = messageEventHandler.saveFileUploadParam(payload);
			//Upload Message Notification in chatrooms
			messageEventHandler.newMessageEvent(savedMessage, sender);
			//Send Messages in chatroom.
			simpMessagingTemplate.convertAndSend("/topic/conversations."+fileParms.getChatRoomId(), messageEventHandler
					.getResponseForClient(sender, savedMessage));
			response.add(messageEventHandler.getResponseForClient(sender, savedMessage));
		}
		return response;
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
