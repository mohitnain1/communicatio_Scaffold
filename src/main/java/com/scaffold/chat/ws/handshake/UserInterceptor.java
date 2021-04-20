package com.scaffold.chat.ws.handshake;
import java.security.Principal;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
 
public class UserInterceptor implements ChannelInterceptor {
 
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
 
        StompHeaderAccessor accessor
                = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
 
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
            System.out.println(raw);
 
            if (raw instanceof Map) {
                Object name = ((Map) raw).get("username");
                System.out.println(name);
                if (name instanceof LinkedList) {
                    accessor.setUser(new User(((LinkedList) name).get(0).toString()));
                }
            }
        }
        System.out.println(message);
        return message;
    }
}

class User implements Principal {
	 
    String name;
 
    public User(String name) {
        this.name = name;
    }
 
    @Override
    public String getName() {
        return name;
    }
}