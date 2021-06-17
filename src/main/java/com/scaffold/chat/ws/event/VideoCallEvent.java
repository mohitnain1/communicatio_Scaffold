package com.scaffold.chat.ws.event;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import com.scaffold.chat.datatransfer.UserDataTransfer;
import com.scaffold.chat.domains.ChatPayload;
import com.scaffold.chat.domains.Member;
import com.scaffold.chat.domains.SignalPayload;
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
	
	public void incomingCall(Message<ChatPayload> message) {
		com.scaffold.chat.domains.Message savedMessage = messageEventHandler.saveMessage(getCallMessage(message),
				messageEventHandler.getHeaderAccessor(message));
		UserDataTransfer user = getUserBasicDetails(savedMessage.getSenderId());
		Map<String, Object> response = new HashMap<String, Object>();	
		if(Objects.nonNull(savedMessage)) {
			response.put("id", savedMessage.getId());
			response.put("sender", user);
			response.put("sendingTime", savedMessage.getSendingTime());
			response.put("content",savedMessage.getContent());
			response.put("contentType", savedMessage.getContentType());
			String chatRoomId = savedMessage.getDestination().replace("/app/call.", "");
			if(Objects.nonNull(savedMessage)) {
				System.out.println(savedMessage.getDestination());
				String destination = String.format("/topic/conversations.", chatRoomId);
				simpMessagingTemplate.convertAndSend(destination, response);
			}
		}
		new Thread(() -> {incomingCallInvitation(savedMessage, user);}).start();
	}
	
	public void incomingCallInvitation(com.scaffold.chat.domains.Message messagePayload, UserDataTransfer sender) {
		Map<String, Object> response = new HashMap<String, Object>();
		String chatRoomId=null;
		if(messagePayload.getDestination().startsWith("/app/call")) {
			chatRoomId= messagePayload.getDestination().replace("/app/call.", "");
			response.put("sender", sender);
			response.put("chat", messagePayload);
			response.put("totalMembers", getTotalMembers(chatRoomId));
			response.put("inCallMembers", null);
			response.put("chatRoomId", chatRoomId);
		}
		if(Objects.nonNull(chatRoomId)) {
			chatRoomRepository.findByChatRoomIdAndIsDeleted(chatRoomId, false).ifPresent(chatRoom ->{				
				List<Long> chatRoomMembersId = chatRoom.getMembers().stream().map(Member::getUserId)
						.filter(memberId -> !memberId.equals(sender.getUserId())).collect(Collectors.toList());
				chatRoomMembersId.forEach(userId->{
					response.put("chatRoomName", chatRoom.getChatRoomName());
					String destinationToNotify = String.format(Destinations.MESSAGE_EVENT_NOTIFICATION.getPath(), userId);
					simpMessagingTemplate.convertAndSend(destinationToNotify, response);
				});
			});
		}
	}
	
	private ChatPayload getCallMessage(Message<ChatPayload> message) {
		ChatPayload payload = message.getPayload();
		if(payload.getContentType().equals(MessageEnum.CALL_INITIATED.getValue())) {
			payload.setContentType(MessageEnum.INCOMING_CALL.getValue());
			payload.setDestination(payload.getDestination());
			payload.setSenderId(payload.getSenderId());
			payload.setUsername(userDetailsRepository.findByUserId(payload.getSenderId()).getUsername());
			payload.setContent("Call started by " + payload.getUsername());
			payload.setSendingTime(new Date().getTime());
			return payload;
		}
		return null;
	}

	public void callAccepted(Message<ChatPayload> message) {
		ChatPayload chatPayload = message.getPayload();
		Map<String, Object> response = new HashMap<String, Object>();	
		if(Objects.nonNull(chatPayload.getSenderId())) {
			UserDataTransfer user = getUserBasicDetails(chatPayload.getSenderId());
			response.put("sender", user);
			response.put("sendingTime", new Date().getTime());
			response.put("content", user.getUsername()+" joined.");
			response.put("contentType", MessageEnum.CALL_ACCEPTED.getValue());
			String destination = String.format(Destinations.VIDEO_CALL.getPath(), getHeaderAccessor(message).getDestination().replace("/app/call.", ""));
			simpMessagingTemplate.convertAndSend(destination, response);
		}
	}
	
	public void callRejected(Message<ChatPayload> message) {
		ChatPayload chatPayload = message.getPayload();
		Map<String, Object> response = new HashMap<String, Object>();	
		if(Objects.nonNull(chatPayload.getSenderId())) {
			UserDataTransfer user = getUserBasicDetails(chatPayload.getSenderId());
			response.put("sender", user);
			response.put("sendingTime", new Date().getTime());
			response.put("content", "Call declined by "+user.getUsername());
			response.put("contentType", MessageEnum.CALL_REJECTED.getValue());
			String destination = String.format(Destinations.VIDEO_CALL.getPath(), getHeaderAccessor(message).getDestination().replace("/app/call.", ""));
			simpMessagingTemplate.convertAndSend(destination, response);
		}
	}
	
	public void callDisconnected(Message<ChatPayload> message) {
		ChatPayload chatPayload = message.getPayload();
		Map<String, Object> response = new HashMap<String, Object>();	
		if(Objects.nonNull(chatPayload.getSenderId())) {
			UserDataTransfer user = getUserBasicDetails(chatPayload.getSenderId());
			response.put("sender", user);
			response.put("sendingTime", new Date().getTime());
			response.put("content", user.getUsername()+" left.");
			response.put("contentType", MessageEnum.CALL_REJECTED.getValue());
			String destination = String.format(Destinations.VIDEO_CALL.getPath(), getHeaderAccessor(message).getDestination().replace("/app/call.", ""));
			simpMessagingTemplate.convertAndSend(destination, response);
		}
	}
	
	public void returnSignal(Message<SignalPayload> message) {
		SignalPayload payload = message.getPayload();
		Map<String, Object> response = new HashMap<String, Object>();	
		if(Objects.nonNull(payload.getSenderId())) {
			UserDataTransfer user = getUserBasicDetails(payload.getSenderId());
			response.put("sender", user);
			response.put("signal", payload.getSignal());
			response.put("contentType", MessageEnum.RETURNING_SIGNAL.getValue());
			String destination = String.format(Destinations.VIDEO_CALL.getPath(), getHeaderAccessor(message).getDestination().replace("/app/call.", ""));
			simpMessagingTemplate.convertAndSend(destination, response);
		}
	}

	public void sendSignal(Message<SignalPayload> message) {
		SignalPayload payload = message.getPayload();
		Map<String, Object> response = new HashMap<String, Object>();	
		if(Objects.nonNull(payload.getSenderId())) {
			UserDataTransfer user = getUserBasicDetails(payload.getSenderId());
			response.put("sender", user);
			response.put("signal", payload.getSignal());
			response.put("userToSignalId", payload.getUserToSignalId());
			response.put("contentType", MessageEnum.SENDING_SIGNAL.getValue());
			String destination = String.format(Destinations.VIDEO_CALL.getPath(), getHeaderAccessor(message).getDestination().replace("/app/call.", ""));
			simpMessagingTemplate.convertAndSend(destination, response);
		}
	}
	
	private List<UserDataTransfer> getTotalMembers(String chatRoomId) {
		List<Member> members = chatRoomRepository.findByChatRoomIdAndIsDeleted(chatRoomId, false).get().getMembers();
		return members.stream().map(memberInIt -> {
			User user = userDetailsRepository.findByUserId(memberInIt.getUserId().longValue());
			UserDataTransfer credentials = new UserDataTransfer(user.getUserId(), user.getImage(),user.getUsername());
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
