package com.scaffold.chat.ws.event;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.scaffold.chat.domains.Member;
import com.scaffold.chat.repository.ChatRoomRepository;
import com.scaffold.chat.service.impl.UserServiceImpl;
import com.scaffold.web.util.Destinations;

@Component
public class SubscriptionEventHandler implements ApplicationListener<SessionSubscribeEvent> {

	@Autowired SimpMessagingTemplate template;
	@Autowired ChatRoomRepository chatRoomRepository;
	@Autowired UserServiceImpl userService;
	
	private static final Logger log = LoggerFactory.getLogger(SubscriptionEventHandler.class);
	
	@Override
	public void onApplicationEvent(SessionSubscribeEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) headerAccessor.getUser();
		try {
			resolveDestinationAndNotifyUsers(headerAccessor, user);
			userLastSeenInRoom(user, headerAccessor);
		}catch(Exception e) {
			log.error("Exception Occured while user last seen updation or notifying subscription event with "
					+ "localised message {}", e.getLocalizedMessage());
		}
	}

	private void userLastSeenInRoom(UsernamePasswordAuthenticationToken user, StompHeaderAccessor headerAccessor) {
		CompletableFuture.supplyAsync(() -> {
			String email = user.getPrincipal().toString();
			long userId = userService.loadUserByEmail(email).getUserId();
			if(headerAccessor.getDestination().startsWith("/topic/conversations")) {
				String chatRoomId = headerAccessor.getDestination().replace("/topic/conversations.", "");
				chatRoomRepository.findByChatRoomIdAndIsDeleted(chatRoomId, false).map(room -> {
					List<Member> members = room.getMembers().stream().map(member -> {
						if (member.getUserId().equals(userId)) {
							member.setLastSeen(LocalDateTime.now());
							return member;
						}
						return member;
					}).collect(Collectors.toList());
					room.setMembers(members);
					chatRoomRepository.save(room);
					return true;
				});
			}
			return false;
		});
	}

	private void resolveDestinationAndNotifyUsers(StompHeaderAccessor headerAccessor, UsernamePasswordAuthenticationToken user) {
		if(headerAccessor.getDestination().startsWith("/topic/conversations")) {
			String chatRoomId = headerAccessor.getDestination().replace("/topic/conversations.", "");
			String destinationToNotify = String.format(Destinations.CHATROOM_JOIN.getPath(), chatRoomId);
			template.convertAndSend(destinationToNotify, user.getPrincipal() + " has just joined.");
		}
	}

}
