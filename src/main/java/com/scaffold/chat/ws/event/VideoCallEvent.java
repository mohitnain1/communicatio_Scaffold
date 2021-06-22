package com.scaffold.chat.ws.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import com.scaffold.chat.datatransfer.UserDataTransfer;
import com.scaffold.chat.domains.ChatPayload;
import com.scaffold.chat.domains.Member;
import com.scaffold.chat.domains.User;
import com.scaffold.chat.repository.ChatRoomRepository;
import com.scaffold.chat.repository.MessageStoreRepository;
import com.scaffold.chat.repository.UserRepository;
import com.scaffold.web.util.Destinations;
import com.scaffold.web.util.MessageEnum;

@Component
public class VideoCallEvent {

	@Autowired public ChatRoomRepository chatRoomRepository;
	@Autowired public MessageStoreRepository messageStoreRepository;
	@Autowired public UserRepository userDetailsRepository;
	@Autowired public SimpMessagingTemplate simpMessagingTemplate;
	@Autowired MessageEventHandler messageEventHandler;
	
	private static final Logger log = LoggerFactory.getLogger(VideoCallEvent.class);
	
	private List<UserDataTransfer> inCallMembers;
	
	public void initiateCall(Message<Map<String, Object>> message) {
		com.scaffold.chat.domains.Message savedMessage = messageEventHandler.saveMessage(getCallMessage(message), messageEventHandler.getHeaderAccessor(message));
		UserDataTransfer sender = getUserBasicDetails(savedMessage.getSenderId());
		inCallMembers=new ArrayList<>();
		inCallMembers.add(sender);
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("id", savedMessage.getId());
		response.put("sender", sender);
		response.put("sendingTime", savedMessage.getSendingTime());
		response.put("content", savedMessage.getContent());
		response.put("contentType", savedMessage.getContentType());
		response.put("signal", message.getPayload().get("signal"));
		String chatRoomId = savedMessage.getDestination().replace("/app/call.", "");
		String destination = String.format("/topic/conversations.", chatRoomId);
		simpMessagingTemplate.convertAndSend(destination, response);
		incomingCallInvitation(savedMessage, sender, message.getPayload().get("signal"));
	}

	public void incomingCallInvitation(com.scaffold.chat.domains.Message messagePayload, UserDataTransfer sender, Object signal) {
		Map<String, Object> response = new HashMap<String, Object>();
		String chatRoomId = null;
		if (messagePayload.getDestination().startsWith("/app/call")) {
			chatRoomId = messagePayload.getDestination().replace("/app/call.", "");
			response.put("sender", sender);
			response.put("totalMembers", getTotalMembers(chatRoomId));
			response.put("inCallMembers", inCallMembers);
			response.put("chatRoomId", chatRoomId);
			response.put("contentType", MessageEnum.INCOMING_CALL);
			response.put("signal", signal);
		}
		if (Objects.nonNull(chatRoomId)) {
			chatRoomRepository.findByChatRoomIdAndIsDeleted(chatRoomId, false).ifPresent(chatRoom -> {
				List<Long> filteredMembers = chatRoom.getMembers().stream().map(Member::getUserId)
						.filter(memberId -> !memberId.equals(sender.getUserId())).collect(Collectors.toList());
				for (Long memberId : filteredMembers) {
					response.put("chatRoomName", chatRoom.getChatRoomName());
					String destinationToNotify = String.format(Destinations.MESSAGE_EVENT_NOTIFICATION.getPath(),memberId);
					simpMessagingTemplate.convertAndSend(destinationToNotify, response);
				}
			});
		}
	}

	private ChatPayload getCallMessage(Message<Map<String, Object>> message) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
		Map<String, Object> payload = message.getPayload();
		Long senderId = Long.parseLong(payload.get("senderId").toString());
		UserDataTransfer user = getUserBasicDetails(senderId);
		return ChatPayload.builder().contentType(MessageEnum.INCOMING_CALL.getValue())
					.destination(headerAccessor.getDestination())
					.senderId(senderId).sendingTime(System.currentTimeMillis())
					.content("Call Started by " +user.getUsername()).build();
	}

	public void callAccepted(Message<Map<String, Object>> message) {
		Map<String, Object> payload = message.getPayload();
		UserDataTransfer user = getUserBasicDetails(Long.parseLong(String.valueOf(payload.get("senderId"))));
		if(Objects.nonNull(user) && !inCallMembers.contains(user)) {
			inCallMembers.add(user);
			Map<String, Object> response = new HashMap<String, Object>();
			response.put("sender", user);
			response.put("sendingTime", new Date().getTime());
			response.put("content", user.getUsername() + " joined.");
			response.put("contentType", payload.get("contentType"));
			response.put("signal", payload.get("signal"));
			response.put("inCallMembers", inCallMembers);
			String chatRoomId = getHeaderAccessor(message).getDestination().replace("/app/call.", "");
			String destination = String.format(Destinations.VIDEO_CALL.getPath(), chatRoomId);
			simpMessagingTemplate.convertAndSend(destination, response);
			responseToInCallMembers(inCallMembers, user, chatRoomId);
			log.info("Call Accepted by "+ user.getUsername());
		}else {
			log.error("Error in call call receving.");
		}
	}
	
	public void responseToInCallMembers(List<UserDataTransfer> inCallMembers, UserDataTransfer user, String chatRoomId) {
		String chatRoomName = chatRoomRepository.findByChatRoomIdAndIsDeleted(chatRoomId, false).get().getChatRoomName();
		Map<String, Object> response = new HashMap<String, Object>();
		if (!(inCallMembers==null)) {inCallMembers.remove(user);}
		response.put("chatRoomName", chatRoomName);
		response.put("chatRoomId", chatRoomId);
		response.put("inCallMembers", inCallMembers);
		response.put("contentType", MessageEnum.IN_CALL_MEMBERS.getValue());
		String destinationToNotify = String.format(Destinations.MESSAGE_EVENT_NOTIFICATION.getPath(),user.getUserId());
		simpMessagingTemplate.convertAndSend(destinationToNotify, response);
	}

	public void callRejected(Message<Map<String, Object>> message) {
		Map<String, Object> payload = message.getPayload();
		UserDataTransfer user = getUserBasicDetails(Long.parseLong(String.valueOf(payload.get("senderId"))));
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("sender", user);
		response.put("sendingTime", new Date().getTime());
		response.put("content", "Call declined by " + user.getUsername());
		response.put("contentType", payload.get("contentType"));
		String destination = String.format(Destinations.VIDEO_CALL.getPath(),getHeaderAccessor(message).getDestination().replace("/app/call.", ""));
		simpMessagingTemplate.convertAndSend(destination, response);
		log.info("Call rejected by "+ user.getUsername());
	}

	public void callDisconnected(Message<Map<String, Object>> message) {
		Map<String, Object> payload = message.getPayload();
		UserDataTransfer user = getUserBasicDetails(Long.parseLong(String.valueOf(payload.get("senderId"))));
		if (!(inCallMembers==null)) {inCallMembers.remove(user);}
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("sender", user);
		response.put("sendingTime", new Date().getTime());
		response.put("content", user.getUsername() + " left.");
		response.put("contentType", payload.get("contentType"));
		response.put("inCallMembers", inCallMembers);
		String destination = String.format(Destinations.VIDEO_CALL.getPath(),getHeaderAccessor(message).getDestination().replace("/app/call.", ""));
		simpMessagingTemplate.convertAndSend(destination, response);
		log.info("Call disconnected by "+ user.getUsername());
	}

	public void returnSignal(Message<Map<String, Object>> message) {
		Map<String, Object> payload = message.getPayload();
		Map<String, Object> response = new HashMap<String, Object>();
		UserDataTransfer user = getUserBasicDetails(Long.parseLong(String.valueOf(payload.get("senderId"))));
		response.put("sender", user);
		response.put("signal", payload.get("signal"));
		response.put("userToSignalId", payload.get("userToSignalId"));
		response.put("contentType", payload.get("contentType"));
		String destinationToNotify = String.format(Destinations.MESSAGE_EVENT_NOTIFICATION.getPath(), payload.get("userToSignalId"));
		simpMessagingTemplate.convertAndSend(destinationToNotify, response);
		log.info("Signal return by "+ user.getUsername());
	}

	public void sendSignal(Message<Map<String, Object>> message) {
		Map<String, Object> payload = message.getPayload();
		Map<String, Object> response = new HashMap<String, Object>();
		UserDataTransfer user = getUserBasicDetails(Long.parseLong(String.valueOf(payload.get("senderId"))));
		response.put("sender", user);
		response.put("signal", payload.get("signal"));
		response.put("userToSignalId", payload.get("userToSignalId"));
		response.put("contentType", payload.get("contentType"));
		String destinationToNotify = String.format(Destinations.MESSAGE_EVENT_NOTIFICATION.getPath(), payload.get("userToSignalId"));
		simpMessagingTemplate.convertAndSend(destinationToNotify, response);
	}
	
	private List<UserDataTransfer> getTotalMembers(String chatRoomId) {
		List<Member> members = chatRoomRepository.findByChatRoomIdAndIsDeleted(chatRoomId, false).get().getMembers();
		return members.stream().map(memberInIt -> {
			User user = userDetailsRepository.findByUserId(memberInIt.getUserId().longValue());
			UserDataTransfer credentials = new UserDataTransfer(user.getUserId(), user.getImage(), user.getUsername());
			credentials.setIsCreator(memberInIt.isCreator());
			credentials.setEmail(user.getEmail());
			return credentials;
		}).collect(Collectors.toList());
	}

	public StompHeaderAccessor getHeaderAccessor(Message<?> message) {
		return StompHeaderAccessor.wrap(message);
	}

	private UserDataTransfer getUserBasicDetails(Long userId) {
		return userDetailsRepository.findByUserId(userId)
				.map(user -> new UserDataTransfer(user.getUserId(), user.getImage(), user.getUsername())).orElse(null);
	}
}
