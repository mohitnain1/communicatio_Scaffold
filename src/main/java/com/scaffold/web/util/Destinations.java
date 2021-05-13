package com.scaffold.web.util;

public enum Destinations {
	CHATROOM_JOIN("/queue/%s.userJoined"), CHATROOM_LEFT("/queue/%s.userLeft"), 
	INVITATION("/topic/%s.invitations"), MESSAGE_EVENT_NOTIFICATION("/topic/%s.message-notifiation"),
	UPDATE_MEMBERS("/topic/conversations.%s");
	
	private String path;

	private Destinations(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return this.path;
	}
}
