package com.scaffold.chat.ws.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.scaffold.security.domains.UserCredentials;
import com.scaffold.web.util.Destinations;

@Component
public class SubscriptionEventHandler implements ApplicationListener<SessionSubscribeEvent> {

	@Autowired SimpMessagingTemplate template;
	
	@Override
	public void onApplicationEvent(SessionSubscribeEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		UserCredentials user = (UserCredentials) headerAccessor.getUser();
		resolveDestinationAndNotifyUsers(headerAccessor, user);
	}

	private void resolveDestinationAndNotifyUsers(StompHeaderAccessor headerAccessor, UserCredentials user) {
		if(headerAccessor.getDestination().startsWith("/topic/conversations")) {
			String chatRoomId = headerAccessor.getDestination().replace("/topic/conversations.", "");
			String destinationToNotify = String.format(Destinations.CHATROOM_JOIN.getPath(), chatRoomId);
			template.convertAndSend(destinationToNotify, user.getUsername() + " has just joined.");
		}
	}

}
