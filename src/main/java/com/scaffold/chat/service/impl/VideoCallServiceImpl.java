package com.scaffold.chat.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.scaffold.chat.datatransfer.UserDataTransfer;
import com.scaffold.chat.domains.ChatRoom;
import com.scaffold.chat.domains.Member;
import com.scaffold.chat.domains.Message;
import com.scaffold.chat.domains.MessageStore;
import com.scaffold.chat.domains.User;
import com.scaffold.chat.repository.ChatRoomRepository;
import com.scaffold.chat.repository.MessageStoreRepository;
import com.scaffold.chat.repository.UserRepository;
import com.scaffold.chat.service.VideoCallService;
import com.scaffold.web.util.Destinations;
import com.scaffold.web.util.MessageEnum;
import com.scaffold.web.util.SimpleIdGenerator;

@Service
public class VideoCallServiceImpl implements VideoCallService {
	
	@Autowired public ChatRoomRepository chatRoomRepository;
	@Autowired public MessageStoreRepository messageStoreRepository;
	@Autowired public UserRepository userDetailsRepository;
	@Autowired public SimpMessagingTemplate simpMessagingTemplate;
	
	private final SimpleIdGenerator idGenerator = new SimpleIdGenerator();
	
	@Override
	public Object startCall(String chatRoomId) {
		Optional<ChatRoom> chatRoom = chatRoomRepository.findByChatRoomIdAndIsDeleted(chatRoomId, false);
		if(chatRoom.isPresent()) {
			List<Member> members = chatRoom.get().getMembers();
			UserDataTransfer caller = getCurrentUserBasicDetails(getCurrentUser().getUsername());
			Message callNotification = videoCallNotification(caller, chatRoom.get(), members);
			return callNotification;
		}
		return null;
	}
	
	private Message videoCallNotification(UserDataTransfer caller, ChatRoom chatRoom, List<Member> members) {
		if (Objects.nonNull(caller)) {
			Message generatedMessage = getVideoCallMessage(caller, chatRoom.getChatRoomId());
			Map<String, Object> response = new HashMap<String, Object>();
			response.put("id", generatedMessage.getId());
			response.put("sender", caller);
			response.put("content", generatedMessage.getContent());
			response.put("sendingTime", Timestamp.valueOf(generatedMessage.getSendingTime()).getTime());
			response.put("contentType", generatedMessage.getContentType());
			if (!members.isEmpty()) {
				simpMessagingTemplate.convertAndSend(generatedMessage.getDestination(), response);
				sendInvitationForCall(caller, chatRoom, response);
				return saveMessageOfVideoCall(generatedMessage, chatRoom.getChatRoomId());
			}	
		}
		return null;
	}

	private Message getVideoCallMessage(UserDataTransfer sender,String chatRoomId) {
		Message message = new Message();
		String messageContent = buildCallMessage(sender);
		String destinationToNotify = String.format(Destinations.START_CALL.getPath(),sender.getUserId());
		message.setDestination(destinationToNotify);
		message.setSenderId(sender.getUserId());
		message.setContent(messageContent);
		message.setContentType(MessageEnum.START_CALL.getValue());
		message.setSendingTime(LocalDateTime.now());
		message.setId(idGenerator.generateRandomId());
		return message;
	}
	
	private void sendInvitationForCall(UserDataTransfer caller, ChatRoom chatRoom, Map<String, Object> response) {
		chatRoomRepository.findByChatRoomIdAndIsDeleted(chatRoom.getChatRoomId(), false).ifPresent(chatRoomData -> {
			List<Long> chatRoomMembersId = chatRoomData.getMembers().stream().map(Member::getUserId)
					.filter(memberId -> !memberId.equals(caller.getUserId())).collect(Collectors.toList());
			chatRoomMembersId.forEach(userId -> {
				response.put("chatRoomName", chatRoomData.getChatRoomName());
				String destinationToNotify = String.format(Destinations.CALL_INVITATION.getPath(), userId);
				simpMessagingTemplate.convertAndSend(destinationToNotify, response);
			});
		});
	}
	
	private String buildCallMessage(UserDataTransfer sender) {
		String message="Call from "+sender.getUsername();
		return message;
	}
	
	public Message saveMessageOfVideoCall(Message message, String chatRoomId) {
		if (!message.equals(null)) {
			MessageStore messageStore = messageStoreRepository.findByChatRoomId(chatRoomId);
			List<Message> messageDetails = messageStore.getMessageDetails();
			messageDetails.add(message);
			messageStore.setMessageDetails(messageDetails);
			messageStoreRepository.save(messageStore);
			return message;
		}
		return null;
	}

	private org.springframework.security.core.userdetails.User getCurrentUser() {
		return (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	private UserDataTransfer getCurrentUserBasicDetails(String email) {
		User currentUser = userDetailsRepository.findByEmailAndIsDeleted(getCurrentUser().getUsername(), false);
		return new UserDataTransfer(currentUser.getUserId(), currentUser.getImage(), currentUser.getUsername());
	}
}
