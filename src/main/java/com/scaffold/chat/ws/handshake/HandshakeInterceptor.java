package com.scaffold.chat.ws.handshake;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.AbstractHandshakeHandler;

public class HandshakeInterceptor extends AbstractHandshakeHandler {

	@Override
	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {
		extractParameters(request, attributes);
		return new  Credentials("aa", "asfsda", "fdasfad,", "fdsafasd");
	}

	private void extractParameters(ServerHttpRequest request, Map<String, Object> attributes) {
		System.out.println("Inside extract parametrs");
		HttpHeaders headers = request.getHeaders();
	}
	
}

class Credentials implements Principal {

	private String username;
	private String password;
	private String email;
	private String imageLink;
	
	

	public Credentials(String username, String password, String email, String imageLink) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.imageLink = imageLink;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImageLink() {
		return imageLink;
	}

	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}

	@Override
	public String getName() {
		return this.username;
	}

}