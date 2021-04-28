package com.scaffold.chat.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaffold.chat.datatransfer.ChatRoomResponse;
import com.scaffold.chat.model.ChatRoom;
import com.scaffold.chat.model.Member;
import com.scaffold.chat.model.MessageStore;
import com.scaffold.chat.model.User;
import com.scaffold.chat.repository.ChatRoomRepository;
import com.scaffold.chat.repository.MessageStoreRepository;
import com.scaffold.chat.repository.UsersDetailRepository;
import com.scaffold.chat.service.ChatRoomService;
import com.scaffold.security.domains.UserCredentials;
import com.scaffold.security.jwt.JwtUtil;
import com.scaffold.web.util.Destinations;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {
	
	@Autowired JwtUtil jwtUtil;
	@Autowired MongoTemplate mongoTemplate;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatRoomServiceImpl.class);

	@Autowired public ChatRoomRepository chatRoomRepository;
	@Autowired public MessageStoreRepository messageStoreRepository;
	@Autowired public UsersDetailRepository userDetailsRepository;
	@Autowired public SimpMessagingTemplate simpMessagingTemplate;
	@Autowired private ObjectMapper mapper;

	@Override
	public ChatRoomResponse createChatRoom(String chatRoomName, List<UserCredentials> chatRoomMembers) {
		Optional<ChatRoom> existingChatRoom = chatRoomRepository.findByChatRoomName(chatRoomName);
		if(existingChatRoom.isPresent()) {
			return null;
		} else {
			saveOrUpdateUsers(chatRoomMembers);	
			
			List<Member> members = chatRoomMembers.stream().map(member-> new Member(member.getUserId(), member.getIsCreator())).collect(Collectors.toList());
			ChatRoom chatRoom = mapChatRoomCreationDetails(chatRoomName, members);		
			
			sendInviteToUsers(chatRoom, chatRoomMembers);
			ChatRoomResponse response = mapper.convertValue(chatRoom, ChatRoomResponse.class);
			response.setTotalMembers(chatRoom.getMembers().size());
			return response;
		}
	}

	private void sendInviteToUsers(ChatRoom chatRoom, List<UserCredentials> chatRoomMembers) {
		HashMap<String, Object> response = new HashMap<>();
		response.put("chatRoomId", chatRoom.getChatRoomId());
		response.put("chatRoomName", chatRoom.getChatRoomName());
		response.put("roomAccessKey", chatRoom.getRoomAccessKey());
		response.put("totalMembers", chatRoom.getMembers().size());
		response.put("creator", chatRoomMembers.stream().filter(member -> member.getIsCreator() == true).findFirst().get());
		
		chatRoom.getMembers().forEach(member -> {
			String destination = String.format(Destinations.INVITATION.getPath(), member.getUserId());
			simpMessagingTemplate.convertAndSend(destination ,response);
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
	public List<UserCredentials> addMembers(String chatRoomId, List<UserCredentials> members) {
		return chatRoomRepository.findByChatRoomId(chatRoomId).map(chatRoom -> {
			chatRoom.getMembers().clear();
			saveOrUpdateUsers(members);
			List<Member> newMembers = userCredentialToMemberMapper(members);			
			chatRoom.setMembers(newMembers);
			chatRoom = chatRoomRepository.save(chatRoom);
			sendInviteToUsers(chatRoom, members);
			LOGGER.info("Members added successfully....");
			return mapChatRoomMembersResponse(chatRoom.getMembers());
		}).orElseGet(ArrayList::new);
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
				userDetailsRepository.save(user);
			}
			else {
				User newUser = new User();
				newUser.setUserId(credentials.getUserId());
				newUser.setUsername(credentials.getUsername());
				newUser.setUserProfilePicture(credentials.getImageLink());
				newUser.setUserLastSeen(LocalDateTime.now());
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
			return credentials;
		}).collect(Collectors.toList());
	}
}
