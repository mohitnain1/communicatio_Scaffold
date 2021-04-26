package com.scaffold.chat.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaffold.chat.datatransfer.ChatRoomResponse;
import com.scaffold.chat.model.ChatRoom;
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

@Service
public class ChatRoomServiceImpl implements ChatRoomService {
	
	@Autowired JwtUtil jwtUtil;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatRoomServiceImpl.class);

	@Autowired public ChatRoomRepository chatRoomRepository;
	@Autowired public MessageStoreRepository messageStoreRepository;
	@Autowired public  UsersDetailRepository userDetailsRepository;
	@Autowired public SimpMessagingTemplate simpMessagingTemplate;
	@Autowired private ObjectMapper mapper;

	@Override
	public ChatRoomResponse createChatRoom(String chatRoomName, UserCredentials chatRoomCreator, List<UserCredentials> chatRoomMembers) {
		saveOrUpdateUsers(chatRoomMembers);
		saveOrUpdateUsers(Arrays.asList(chatRoomCreator));
		
		List<Long> chatRoomMembersId = chatRoomMembers.stream().map(UserCredentials::getUserId).collect(Collectors.toList());
		
		ChatRoom chatRoom = mapChatRoomCreationDetails(chatRoomName, chatRoomCreator.getUserId(), chatRoomMembersId);		
		
		sendInviteToUsers(chatRoom, chatRoomMembersId);
		ChatRoomResponse response = mapper.convertValue(chatRoom, ChatRoomResponse.class);
		response.setMembers(mapChatRoomMembersResponse(chatRoom.getChatRoomMembersId()));
		response.setCreator(mapChatRoomMembersResponse(Arrays.asList(chatRoomCreator.getUserId())).get(0));
		return response;
	}

	private void sendInviteToUsers(ChatRoom chatRoom, List<Long> chatRoomMembersId) {
		ChatRoomResponse response = mapper.convertValue(chatRoom, ChatRoomResponse.class);
		chatRoomMembersId.forEach(member -> {
			String destination = String.format(Destinations.INVITATION.getPath(), member.toString());
			simpMessagingTemplate.convertAndSend(destination ,response);
		});
	}

	private ChatRoom mapChatRoomCreationDetails(String chatRoomName, long chatRoomCreatorId,
			List<Long> chatRoomMembersId) {
		ChatRoom chatRoom = new ChatRoom(chatRoomName, chatRoomCreatorId, chatRoomMembersId);
		chatRoom.setChatRoomType("Developer-Testing");
		chatRoom.setChatRoomCreationDate(LocalDateTime.now());
		chatRoom.setChatRoomLastConversationDate(LocalDateTime.now());
		chatRoom.setChatRoomId(createChatRoomId());
		chatRoom.setMessageStore(generateMessageStore(chatRoom.getChatRoomId()));
		chatRoom.setRoomAccessKey(jwtUtil.generateToken(chatRoom.getChatRoomId()));
		chatRoom = chatRoomRepository.save(chatRoom);
		chatRoomMembersId.add(chatRoomCreatorId);
		addSubscribedChatRoomToUser(chatRoomMembersId, chatRoom.getChatRoomId());
		chatRoomMembersId.remove(chatRoomCreatorId);	
		return chatRoom;
	}

	private void addSubscribedChatRoomToUser(List<Long> chatRoomMembersId, String chatRoomId) {
		chatRoomMembersId.stream().forEach(userId -> {
			userDetailsRepository.findByUserId(userId).ifPresent(user -> {
				try {
					List<String> chatRooms = user.getChatRoomIds();
					if(!chatRooms.contains(chatRoomId)) {
						chatRooms.add(chatRoomId);
					}
					user.setChatRoomIds(chatRooms);
				} catch (NullPointerException e) {
					user.setChatRoomIds(Arrays.asList(chatRoomId));
				}
				userDetailsRepository.save(user);
			});
		});
	}
	
	private void removeChatRoomFromUser(List<Long> chatRoomMembersId, String chatRoomId) {
		chatRoomMembersId.stream().forEach(userId -> {
			userDetailsRepository.findByUserId(userId).ifPresent(user -> {
				List<String> chatRooms = user.getChatRoomIds();
				chatRooms.removeIf(room -> room.equalsIgnoreCase(chatRoomId));
				user.setChatRoomIds(chatRooms);
				userDetailsRepository.save(user);
			});
		});
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
		messageStore.setMessageDetails(Arrays.asList(generateWelcomeMessage(chatRoomId)));
		MessageStore savedMessageStore = messageStoreRepository.save(messageStore);
		return savedMessageStore;
	}

	protected Message generateWelcomeMessage(String chatRoomId) {
		Message messageDetail = new Message();
		messageDetail.setMessageDestination("/topic/" + chatRoomId);
		messageDetail.setMessageSenderId(1L);
		messageDetail.setMessageSendingTime(LocalDateTime.now());
		messageDetail.setMesssageContent("Hello everyone!!");
		return messageDetail;
	}

	@Override
	public List<UserCredentials> addMembers(String chatRoomId, List<UserCredentials> members) {
		return chatRoomRepository.findByChatRoomId(chatRoomId).map(chatRoom -> {
			List<Long> savedMemebersId = chatRoom.getChatRoomMembersId();
			saveOrUpdateUsers(members);
			List<Long> newMembersId = members.stream().map(UserCredentials::getUserId).collect(Collectors.toList());
			
			savedMemebersId.addAll(newMembersId);
			chatRoom.setChatRoomMembersId(savedMemebersId.stream().distinct().collect(Collectors.toList()));
			chatRoom = chatRoomRepository.save(chatRoom);
			addSubscribedChatRoomToUser(savedMemebersId, chatRoomId);
			sendInviteToUsers(chatRoom, newMembersId);
			LOGGER.info("Members added successfully....");
			return mapChatRoomMembersResponse(chatRoom.getChatRoomMembersId());
		}).orElseGet(ArrayList::new);
	}

	private List<UserCredentials> mapChatRoomMembersResponse(List<Long> chatRoomMembersId) {
		return chatRoomMembersId.stream().map(userId -> {
			User user = userDetailsRepository.findByUserId(userId.longValue());
			UserCredentials credentials = mapper.convertValue(user, UserCredentials.class);
			credentials.setImageLink(user.getUserProfilePicture());
			return credentials;
		}).collect(Collectors.toList());
	}

	private void saveOrUpdateUsers(List<UserCredentials> members) {
		members.stream().forEach(credentials -> {
			User user = userDetailsRepository.findByUserId(credentials.getUserId());
			if(Objects.nonNull(user)) {
				user.setUserProfilePicture(credentials.getImageLink());
				user.setUsername(credentials.getUsername());
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
		User user = userDetailsRepository.findByUserId(userId);
		if(Objects.nonNull(user)) {
			//Removing duplicate chat-rooms from list.
			List<String> userChatRooms = user.getChatRoomIds();
			if(Objects.nonNull(userChatRooms) && !userChatRooms.isEmpty()) {
				userChatRooms = userChatRooms.stream().distinct().collect(Collectors.toList());
				return mapUserChatRoomResponse(userChatRooms);
			} else {
				return new ArrayList<ChatRoomResponse>();
			}
		} else {
			return null;
		}
	}

	private List<ChatRoomResponse> mapUserChatRoomResponse(List<String> userChatRooms) {
		return userChatRooms.stream().map(chatRoomId -> {
			return chatRoomRepository.findByChatRoomId(chatRoomId)
					.map(chatRoom -> {
						ChatRoomResponse chatRoomResponse = mapper.convertValue(chatRoom, ChatRoomResponse.class);
						chatRoomResponse.setMembers(mapChatRoomMembersResponse(chatRoom.getChatRoomMembersId()));
						chatRoomResponse.setCreator(mapChatRoomMembersResponse(Arrays.asList(chatRoom.getChatRoomCreatorId())).get(0));
						return chatRoomResponse;
					}).orElseGet(ChatRoomResponse::new);
		}).filter(response -> !response.getChatRoomName().equals("")).collect(Collectors.toList());
	}
}
