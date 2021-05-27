package com.scaffold.chat.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaffold.chat.datatransfer.ChatRoomResponse;
import com.scaffold.chat.datatransfer.ChatRoomUpdateParams;
import com.scaffold.chat.datatransfer.UserDataTransfer;
import com.scaffold.chat.domains.ChatRoom;
import com.scaffold.chat.domains.Member;
import com.scaffold.chat.domains.Message;
import com.scaffold.chat.domains.MessageStore;
import com.scaffold.chat.domains.User;
import com.scaffold.chat.repository.ChatRoomRepository;
import com.scaffold.chat.repository.MessageStoreRepository;
import com.scaffold.chat.repository.UserRepository;
import com.scaffold.chat.service.ChatRoomService;
import com.scaffold.chat.ws.event.MessageEventHandler;
import com.scaffold.security.jwt.JwtUtil;
import com.scaffold.web.util.Destinations;
import com.scaffold.web.util.MessageEnum;
import com.scaffold.web.util.Response;
import com.scaffold.web.util.SimpleIdGenerator;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {
	
	@Autowired JwtUtil jwtUtil;
	@Autowired MongoTemplate mongoTemplate;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatRoomServiceImpl.class);
	private final SimpleIdGenerator idGenerator = new SimpleIdGenerator();

	@Autowired public ChatRoomRepository chatRoomRepository;
	@Autowired public MessageStoreRepository messageStoreRepository;
	@Autowired public UserRepository userDetailsRepository;
	@Autowired public SimpMessagingTemplate simpMessagingTemplate;
	@Autowired private ObjectMapper mapper;
	@Autowired private MessageEventHandler messageEventHandler;

	@Override
	public ResponseEntity<Object> createChatRoom(String chatRoomName, List<Long> chatRoomMembers) throws IllegalArgumentException, Exception {
		Optional<ChatRoom> existingChatRoom = chatRoomRepository.findByChatRoomNameAndIsDeleted(chatRoomName, false);
		if(existingChatRoom.isPresent()) {
			if(!isRoomMember(existingChatRoom.get())) {
				throw new Exception("You're not a member of this room. Please connect room admin.");
			}
			ChatRoomResponse response = mapper.convertValue(existingChatRoom.get(), ChatRoomResponse.class);
			response.setTotalMembers(existingChatRoom.get().getMembers().size());
			return Response.generateResponse(HttpStatus.ACCEPTED, response, "Chatroom name already exists.", false);
		} else {		
			List<Long> membersToAdd = chatRoomMembers.stream().filter( id -> userExists(id)).distinct().collect(Collectors.toList());
			if(membersToAdd.size()>=2) {
				List<Member> members = membersToAdd.stream().map(member-> new Member(member, isOperatingUser(member))).collect(Collectors.toList());
				ChatRoom chatRoom = mapChatRoomCreationDetails(chatRoomName, members);		
				
				sendInviteToUsers(chatRoom, members.stream().filter(member -> !member.isCreator()).collect(Collectors.toList()));
				ChatRoomResponse response = mapper.convertValue(chatRoom, ChatRoomResponse.class);
				response.setTotalMembers(chatRoom.getMembers().size());
				return Response.generateResponse(HttpStatus.CREATED, response, "Chatroom Created", true);
			}
			return Response.generateResponse(HttpStatus.LENGTH_REQUIRED, null, "Atleast two memebrs required for chatroom creation.", false);
		}
	}

	private boolean isRoomMember(ChatRoom chatRoom) throws Exception {
		String email = getCurrentUser().getUsername();
		User currentUser = userDetailsRepository.findByEmailAndIsDeleted(email, false);
		return chatRoom.getMembers().stream().anyMatch(roomInSt -> roomInSt.getUserId().equals(currentUser.getUserId()));
	}

	private boolean isOperatingUser(Long member) {
		String email = getCurrentUser().getUsername();
		User user = userDetailsRepository.findByEmailAndIsDeleted(email, false);
		if(member.equals(user.getUserId())) {
			return true;
		}
		return false;
	}

	private org.springframework.security.core.userdetails.User getCurrentUser() {
		return (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	private void sendInviteToUsers(ChatRoom chatRoom, List<Member> membersSendInvite) {
		User currentUser = userDetailsRepository.findByEmailAndIsDeleted(getCurrentUser().getUsername(), false);
		HashMap<String, Object> response = new HashMap<>();
		response.put("chatRoomId", chatRoom.getChatRoomId());
		response.put("chatRoomName", chatRoom.getChatRoomName());
		response.put("roomAccessKey", chatRoom.getRoomAccessKey());
		response.put("totalMembers", chatRoom.getMembers().size());
		response.put("creator", new UserDataTransfer(currentUser.getUserId(), currentUser.getImage(), currentUser.getUsername()));
		membersSendInvite.forEach(member -> {
			String destination = String.format(Destinations.INVITATION.getPath(), member.getUserId());
			simpMessagingTemplate.convertAndSend(destination ,response);
		});
	}

	private ChatRoom mapChatRoomCreationDetails(String chatRoomName, List<Member> members) {
		ChatRoom chatRoom = new ChatRoom(chatRoomName, members);
		chatRoom.setCreationDate(LocalDateTime.now());
		chatRoom.setLastConversation(LocalDateTime.now());
		chatRoom.setChatRoomId(createChatRoomId());
		chatRoom.setMessageStore(generateMessageStore(chatRoom.getChatRoomId()));
		chatRoom.setRoomAccessKey(jwtUtil.generateToken(chatRoom.getChatRoomId()));
		chatRoom = chatRoomRepository.save(chatRoom);
		return chatRoom;
	}

	private String createChatRoomId() {
		String roomName = UUID.randomUUID().toString().substring(0, 6)+System.
				currentTimeMillis()+UUID.randomUUID().toString().substring(0, 6);
		roomName=roomName.replaceAll("[-+.^:,]","");
		return roomName;
	}

	private MessageStore generateMessageStore(String chatRoomId) {
		MessageStore messageStore = new MessageStore();
		messageStore.setChatRoomId(chatRoomId);
		MessageStore savedMessageStore = messageStoreRepository.save(messageStore);
		return savedMessageStore;
	}

	@Override
	public List<UserDataTransfer> updateUserInChatRoom(ChatRoomUpdateParams params) {
		return chatRoomRepository.findByChatRoomIdAndIsDeleted(params.getChatRoomId(), false).map(chatRoom -> {
			UserDataTransfer sender = getCurrentUserBasicDetails(getCurrentUser().getUsername());
			List<Long> usersToAdd = params.getMembers().getAdd().stream().filter(id -> userExists(id)).collect(Collectors.toList());
			List<Long> usersToRemove = params.getMembers().getRemove();
			List<Member> existingMembers = chatRoom.getMembers();
			
			usersToRemove.removeIf(user -> usersToRemove.contains(sender.getUserId()));
			usersToAdd.removeIf(userId -> existingMembers.stream().anyMatch(mem -> mem.getUserId().equals(userId)));
			params.getMembers().setAdd(usersToAdd);
			params.getMembers().setRemove(usersToRemove);
			
			List<Member> updatedMembersList = resolveChatRoomMembers(params, existingMembers);
			List<Member> updatedUniqueList = updatedMembersList.stream().distinct().collect(Collectors.toList());
			chatRoom.setMembers(updatedUniqueList);
			updatedUserNotification(params);
			chatRoom = chatRoomRepository.save(chatRoom);
			sendInviteToUsers(chatRoom, userIdToMemberMapper(params.getMembers().getAdd()));
			return memberToUserCredential(chatRoom.getMembers());
		}).orElseGet(ArrayList::new);
	}

	private Message updatedUserNotification(ChatRoomUpdateParams params) {
		String chatRoomId = params.getChatRoomId();
		List<Long> usersToAdd = params.getMembers().getAdd().stream().filter(id -> userExists(id)).collect(Collectors.toList());
		List<Member> toAdd = userIdToMemberMapper(usersToAdd);
		List<Member> toRemove = userIdToMemberMapper(params.getMembers().getRemove());
		UserDataTransfer sender = getCurrentUserBasicDetails(getCurrentUser().getUsername());
		
		if (Objects.nonNull(sender)) {
			Message generatedMessage = getUpdateMemberMessage(chatRoomId, sender, toAdd, toRemove);
			Map<String, Object> response = new HashMap<String, Object>();
			response.put("id", generatedMessage.getId());
			response.put("sender", sender);
			response.put("content", generatedMessage.getContent());
			response.put("sendingTime", Timestamp.valueOf(generatedMessage.getSendingTime()).getTime());
			response.put("contentType", generatedMessage.getContentType());
			
			if (!toAdd.isEmpty() || !toRemove.isEmpty()) {
				simpMessagingTemplate.convertAndSend(generatedMessage.getDestination(), response);
				messageEventHandler.newMessageEvent(generatedMessage, sender);
				return saveMessageOfUpdatedUser(generatedMessage, chatRoomId);
			}	
		}
		return null;
	}
	
	private Message getUpdateMemberMessage(String chatRoomId, UserDataTransfer sender, List<Member> toAdd, List<Member> toRemove) {
		Message message = new Message();
		String messageContent = buildMessage(sender, toAdd, toRemove);
		String destinationToNotify = String.format(Destinations.UPDATE_MEMBERS.getPath(), chatRoomId);
		message.setDestination(destinationToNotify);
		message.setSenderId(sender.getUserId());
		message.setContent(messageContent);
		message.setContentType(MessageEnum.UPDATE_MEMBER.getValue());
		message.setSendingTime(LocalDateTime.now());
		message.setId(idGenerator.generateRandomId());
		return message;
	}
	
	private String buildMessage(UserDataTransfer sender, List<Member> toAdd, List<Member> toRemove) {
		StringBuffer messageContent = new StringBuffer();
		StringBuilder usernamesToAdd = new StringBuilder();
		StringBuilder usernameToRemove = new StringBuilder();
		
		if (!toAdd.isEmpty() && !toRemove.isEmpty()) {
			toAdd.forEach(addUser -> usernamesToAdd.append(getUserBasicDetails(addUser).getUsername() + " |"));
			toRemove.forEach(removeUser -> usernameToRemove.append(getUserBasicDetails(removeUser).getUsername() + " | "));
			return messageContent.append(sender.getUsername()).append(" added ").append(usernamesToAdd.toString())
					.append(" and ").append("removed ").append(usernameToRemove.toString()).toString();
		}
		else if (!toAdd.isEmpty()) {
			toAdd.forEach((addUser) -> usernamesToAdd.append(getUserBasicDetails(addUser).getUsername() + " | "));
			messageContent.append(sender.getUsername()).append(" added ").append(usernamesToAdd.toString());
			return messageContent.toString();
		}
		else if (!toRemove.isEmpty()) {
			toRemove.forEach(removeUser -> usernameToRemove.append(getUserBasicDetails(removeUser).getUsername() + " | "));
			return messageContent.append(sender.getUsername()).append(" removed ").append(usernameToRemove.toString()).toString();
		} 
		return messageContent.toString();
	}
		
	public Message saveMessageOfUpdatedUser(Message message, String chatRoomId) {
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
	
	private UserDataTransfer getCurrentUserBasicDetails(String email) {
		User currentUser = userDetailsRepository.findByEmailAndIsDeleted(getCurrentUser().getUsername(), false);
		return new UserDataTransfer(currentUser.getUserId(), currentUser.getImage(), currentUser.getUsername());
	}

	private UserDataTransfer getUserBasicDetails(Member member) {
		return userDetailsRepository.findByUserId(member.getUserId())
				.map(user -> new UserDataTransfer(user.getUserId(), user.getImage(), user.getUsername())).orElse(null);
	}
	
	private List<Member> resolveChatRoomMembers(ChatRoomUpdateParams params, List<Member> existing) {
		List<Member> toAdd = userIdToMemberMapper(params.getMembers().getAdd());
		List<Long> toRemove = params.getMembers().getRemove();
		//Remove admin user if present in remove list
		if(!toRemove.isEmpty()) {
			existing.removeIf(member -> (toRemove.contains(member.getUserId()) && !member.isCreator()));
		}
		existing.addAll(toAdd);
		return existing;
	}

	public final List<Member> userIdToMemberMapper(List<Long> userId) {
		return userId.stream().map(id -> new Member(id, false)).collect(Collectors.toList());
	}
	
	public final boolean userExists(Long userId) {
		return userDetailsRepository.findByUserId(userId).map(user -> true).orElse(false);
	}

	@Override
	public List<ChatRoomResponse> userChatRooms(long userId) {
		return mongoTemplate.query(ChatRoom.class)
				.matching(Criteria.where("members.userId").is(userId)
						.andOperator(Criteria.where("isDeleted").is(false)))
				.all().stream().map(chatRoom -> {
					ChatRoomResponse chatRoomResponse = mapper.convertValue(chatRoom, ChatRoomResponse.class);
					chatRoomResponse.setTotalMembers(chatRoom.getMembers().size());
					chatRoomResponse.setUnreadCount(getUserUnreadCount(chatRoom, userId));
					return chatRoomResponse;
				}).collect(Collectors.toList());
	}
	
	private List<UserDataTransfer> memberToUserCredential(List<Member> member) {
		return member.stream().map(memberInIt -> {
			User user = userDetailsRepository.findByUserId(memberInIt.getUserId().longValue());
			UserDataTransfer credentials = new UserDataTransfer(user.getUserId(), user.getImage(),user.getUsername());
			credentials.setIsCreator(memberInIt.isCreator());
			credentials.setEmail(user.getEmail());
			return credentials;
		}).collect(Collectors.toList());
	}

	@Override
	public boolean deleteChatRoom(String chatRoomId) {
		return chatRoomRepository.findByChatRoomIdAndIsDeleted(chatRoomId, false).map(room -> {
			room.setDeleted(true);
			chatRoomRepository.save(room);
			return true;
		}).orElse(false);
	}
	
	public long getUserUnreadCount(ChatRoom chatRoom, long userId) {
		Member member = chatRoom.getMembers().stream().filter(memberInSt -> memberInSt.getUserId().equals(userId)).findFirst().get();
		if(Objects.isNull(member.getLastSeen())) {
			return chatRoom.getMessageStore().getMessageDetails().size();
		} else {
			return chatRoom.getMessageStore().getMessageDetails().stream()
					.filter(message -> message.getSendingTime().isAfter(member.getLastSeen()))
					.count();
		}
	}
}
