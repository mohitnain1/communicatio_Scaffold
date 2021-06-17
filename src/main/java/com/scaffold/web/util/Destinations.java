package com.scaffold.web.util;

public enum Destinations {
	CHATROOM_JOIN("/queue/%s.userJoined"), CHATROOM_LEFT("/queue/%s.userLeft"), 
	INVITATION("/topic/invitations.%s"), MESSAGE_EVENT_NOTIFICATION("/topic/message-notification.%s"),
	UPDATE_MEMBERS("/topic/conversations.%s"), VIDEO_CALL("/topic/call.%s");
	
	private String path;

	private Destinations(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return this.path;
	}
}
