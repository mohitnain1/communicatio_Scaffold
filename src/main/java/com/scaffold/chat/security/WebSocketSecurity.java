package com.scaffold.chat.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurity extends AbstractSecurityWebSocketMessageBrokerConfigurer {

	@Override
	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
		messages.simpTypeMatchers(SimpMessageType.DISCONNECT).permitAll()
			.anyMessage().authenticated();
	}

	@Override
	protected boolean sameOriginDisabled() {
		return true;
	}
}
