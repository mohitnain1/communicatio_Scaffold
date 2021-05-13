package com.scaffold.chat.service.impl;

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
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaffold.chat.datatransfer.ChatRoomResponse;
import com.scaffold.chat.datatransfer.ChatRoomUpdateParams;
import com.scaffold.chat.model.ChatRoom;
import com.scaffold.chat.model.Member;
import com.scaffold.chat.model.Message;
import com.scaffold.chat.model.MessageStore;
import com.scaffold.chat.model.User;
import com.scaffold.chat.repository.ChatRoomRepository;
import com.scaffold.chat.repository.MessageStoreRepository;
import com.scaffold.chat.repository.UsersDetailRepository;
import com.scaffold.chat.service.ChatRoomService;
import com.scaffold.security.domains.UserCredentials;
import com.scaffold.security.jwt.JwtUtil;
import com.scaffold.web.util.Destinations;
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
	@Autowired public UsersDetailRepository userDetailsRepository;
	@Autowired public SimpMessagingTemplate simpMessagingTemplate;
	@Autowired private ObjectMapper mapper;

	@Override
	public ResponseEntity<Object> createChatRoom(String chatRoomName, List<UserCredentials> chatRoomMembers) {
		Optional<ChatRoom> existingChatRoom = chatRoomRepository.findByChatRoomName(chatRoomName);
		if(existingChatRoom.isPresent()) {
			ChatRoomResponse response = mapper.convertValue(existingChatRoom.get(), ChatRoomResponse.class);
			response.setTotalMembers(existingChatRoom.get().getMembers().size());
			return Response.generateResponse(HttpStatus.CONFLICT, response, "Chatroom name already exists.", false);
		} else {
			saveOrUpdateUsers(chatRoomMembers);	
			
			List<Member> members = chatRoomMembers.stream().map(member-> new Member(member.getUserId(), member.getIsCreator())).collect(Collectors.toList());
			ChatRoom chatRoom = mapChatRoomCreationDetails(chatRoomName, members);		
			
			sendInviteToUsers(chatRoom, chatRoomMembers);
			ChatRoomResponse response = mapper.convertValue(chatRoom, ChatRoomResponse.class);
			response.setTotalMembers(chatRoom.getMembers().size());
			return Response.generateResponse(HttpStatus.CREATED, response, "Chatroom Created", true);
		}
	}

	private void sendInviteToUsers(ChatRoom chatRoom, List<UserCredentials> chatRoomMembers) {
		HashMap<String, Object> response = new HashMap<>();
		response.put("chatRoomId", chatRoom.getChatRoomId());
		response.put("chatRoomName", chatRoom.getChatRoomName());
		response.put("roomAccessKey", chatRoom.getRoomAccessKey());
		response.put("totalMembers", chatRoom.getMembers().size());
		response.put("creator", chatRoomMembers.stream().filter(member -> member.getIsCreator() == true).findAny().orElse(null));
		
		chatRoom.getMembers().forEach(member -> {
			if(!member.isCreator()) {
				String destination = String.format(Destinations.INVITATION.getPath(), member.getUserId());
				simpMessagingTemplate.convertAndSend(destination ,response);
			}
		});
	}

	private ChatRoom mapChatRoomCreationDetails(String chatRoomName, List<Member> members) {
		ChatRoom chatRoom = new ChatRoom(chatRoomName, members);
		chatRoom.setChatRoomCreationDate(LocalDateTime.now());
		chatRoom.setChatRoomLastConversationDate(LocalDateTime.now());
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
	public List<UserCredentials> updateMembers(ChatRoomUpdateParams params) {
		return chatRoomRepository.findByChatRoomId(params.getChatRoomId()).map(chatRoom -> {
			saveOrUpdateUsers(params.getMembers().getAdd());
			List<Member> updatedMembersList = resolveChatRoomMembers(params, chatRoom.getMembers());
			List<Member> updatedUniqueList = updatedMembersList.stream().distinct().collect(Collectors.toList());
			chatRoom.setMembers(updatedUniqueList);
			new Thread(() -> UpdatedUserNotification(params)).start();
			chatRoom = chatRoomRepository.save(chatRoom);
			sendInviteToUsers(chatRoom, params.getMembers().getAdd());
			LOGGER.info("Members added successfully...");
			return mapChatRoomMembersResponse(chatRoom.getMembers());
		}).orElseGet(ArrayList::new);
	}

	private List<Member> resolveChatRoomMembers(ChatRoomUpdateParams params, List<Member> existing) {
		List<Member> toAdd = userCredentialToMemberMapper(params.getMembers().getAdd());
		List<Member> toRemove = userCredentialToMemberMapper(params.getMembers().getRemove());
		if(!toRemove.isEmpty()) {
			existing.removeIf(userId -> (toRemove.contains(userId) && !userId.isCreator()));
			LOGGER.info("Members removed successfully...");
		}
		existing.addAll(toAdd);
		return existing;
	}

	private Message UpdatedUserNotification(ChatRoomUpdateParams params) {
		String messageOfAdd="";
		String messageOfRemove="";
		String chatRoomId = params.getChatRoomId();
		long senderId = params.getSenderId();
		
		List<UserCredentials> toAdd = params.getMembers().getAdd();
		List<UserCredentials> toRemove = params.getMembers().getRemove();
		
		if(!toAdd.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			toAdd.forEach((addUser) -> {builder.append(addUser.getUsername() + ", ");});
			messageOfAdd = builder.toString() + " have been added";
		}
		
		if(!toRemove.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			toRemove.forEach((removeUser) -> {builder.append(removeUser.getUsername() + ",");});
			messageOfRemove = builder.toString()+ " have been removed";
		}
		
		UserCredentials sender = fetchUpdaterDataWithDatabase(chatRoomId, senderId);
		if(Objects.nonNull(sender)) {
			Message generatedMessage = generateMessageForUpdatedUser(chatRoomId, sender, messageOfAdd, messageOfRemove);
			Map<String, Object> updateMessage = new HashMap<String, Object>();
			updateMessage.put("id", generatedMessage.getId());
			updateMessage.put("sender", sender);
			updateMessage.put("content", generatedMessage.getContent());
			updateMessage.put("sendingTime", generatedMessage.getSendingTime());
			
			simpMessagingTemplate.convertAndSend(generatedMessage.getDestination(), updateMessage);
			String destinationToNotify = String.format(Destinations.MESSAGE_EVENT_NOTIFICATION.getPath(),chatRoomId);
			simpMessagingTemplate.convertAndSend(destinationToNotify, updateMessage);
			return saveMessageOfUpdatedUser(generatedMessage, chatRoomId);
		}
		return null;
	}

	private UserCredentials fetchUpdaterDataWithDatabase(String chatRoomId, long senderId) {
		Optional<ChatRoom> chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId);
		List<Member> members = chatRoom.get().getMembers();
		Member senderData = members.stream().filter(sender-> sender.getUserId().equals(senderId)).collect(Collectors.toList()).get(0);
		UserCredentials credentials = new UserCredentials();
		if(Objects.nonNull(senderData)) {
			User userData = userDetailsRepository.findByUserId(senderId);
			credentials.setUserId(userData.getUserId());
			credentials.setUsername(userData.getUsername());
			credentials.setIsCreator(senderData.isCreator());
			credentials.setImageLink(userData.getUserProfilePicture());
			credentials.setEmail(userData.getEmail());
		}
		return credentials;
	}

	private Message generateMessageForUpdatedUser(String chatRoomId, UserCredentials sender, String messageOfAdd, String messageOfRemove) {
		long senderId = sender.getUserId();
		String senderName = sender.getUsername();
		String messageContent="";
		if(!messageOfAdd.isEmpty() && !messageOfRemove.isEmpty()) {
			messageContent=messageOfAdd + " And " + messageOfRemove +" by " +senderName;
		}else if(messageOfAdd.isEmpty()) {
			messageContent= messageOfRemove +" by " +senderName;
		}else if(messageOfRemove.isEmpty()){
			messageContent=messageOfAdd +" by " +senderName;
		}
		
		Message messageDetail = new Message();
		String destinationToNotify = String.format(Destinations.UPDATE_MEMBERS.getPath(),chatRoomId);
		messageDetail.setDestination(destinationToNotify);
		messageDetail.setSenderId(senderId);
		messageDetail.setContent(messageContent);
		messageDetail.setContentType("update_Member");
		messageDetail.setSendingTime(LocalDateTime.now());
		messageDetail.setId(idGenerator.generateRandomId());
		return messageDetail;
	}

	public Message saveMessageOfUpdatedUser(Message message, String chatRoomId) {
		if(!message.equals(null)) {			
			MessageStore messageStore = messageStoreRepository.findByChatRoomId(chatRoomId);
			messageStore.addMessage(message);
			messageStoreRepository.save(messageStore);
			return message;
		}
		return null;
	}

	private List<Member> userCredentialToMemberMapper(List<UserCredentials> members) {
		return members.stream().map(mem -> new Member(mem.getUserId(), mem.getIsCreator()))
				.collect(Collectors.toList());
	}

	private void saveOrUpdateUsers(List<UserCredentials> members) {
		members.stream().forEach(credentials -> {
			User user = userDetailsRepository.findByUserId(credentials.getUserId());
			if(Objects.nonNull(user)) {
				if (!credentials.getImageLink().equals("")) {
				    user.setUserProfilePicture(credentials.getImageLink());
				}
				if (!credentials.getUsername().equals("")) {
				   user.setUsername(credentials.getUsername());
				}
				if (!credentials.getEmail().equals("")) {
					user.setEmail(credentials.getEmail());
				}
				userDetailsRepository.save(user);
			}
			else {
				User newUser = new User();
				newUser.setUserId(credentials.getUserId());
				newUser.setUsername(credentials.getUsername());
				newUser.setUserProfilePicture(credentials.getImageLink());
				newUser.setUserLastSeen(LocalDateTime.now());
				newUser.setEmail(credentials.getEmail());
				LOGGER.info("Saved new User");
				userDetailsRepository.save(newUser);
			}
		});
	}

	@Override
	public List<ChatRoomResponse> userChatRooms(long userId) {
		return mongoTemplate.query(ChatRoom.class)
				.matching(Criteria.where("members.userId").is(userId))
				.all().stream().map(chatRoom -> {
					ChatRoomResponse chatRoomResponse = mapper.convertValue(chatRoom, ChatRoomResponse.class);
					chatRoomResponse.setTotalMembers(chatRoom.getMembers().size());
					return chatRoomResponse;
				}).collect(Collectors.toList());
	}
	
	private List<UserCredentials> mapChatRoomMembersResponse(List<Member> member) {
		return member.stream().map(memberInIt -> {
			User user = userDetailsRepository.findByUserId(memberInIt.getUserId().longValue());
			UserCredentials credentials = new UserCredentials(user.getUserId(), user.getUserProfilePicture(),user.getUsername());
			credentials.setIsCreator(memberInIt.isCreator());
			credentials.setEmail(user.getEmail());
			return credentials;
		}).collect(Collectors.toList());
	}

	@Override
	public List<UserCredentials> removeMembers(String chatRoomId, List<UserCredentials> members) {
		return chatRoomRepository.findByChatRoomId(chatRoomId).map(chatRoom -> {
			List<Member> memberToRemove = userCredentialToMemberMapper(members);
			List<Member> existingMembers = chatRoom.getMembers();
			List<Member> updatedMemberList = existingMembers.stream().filter(memberInIt -> !memberToRemove.contains(memberInIt)).collect(Collectors.toList());
			chatRoom.setMembers(updatedMemberList);
			chatRoomRepository.save(chatRoom);
			return mapChatRoomMembersResponse(chatRoom.getMembers());
		}).orElseGet(ArrayList::new);
	}
}
