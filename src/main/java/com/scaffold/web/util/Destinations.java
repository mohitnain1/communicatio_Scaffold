package com.scaffold.web.util;

public enum Destinations {
	CHATROOM_JOIN("/queue/%s.userJoined"), CHATROOM_LEFT("/queue/%s.userLeft"), 
	INVITATION("/topic/%s.invitations"), MESSAGE_EVENT_NOTIFICATION("/topic/%s.message-notification"),
	UPDATE_MEMBERS("/topic/conversations.%s"), START_CALL("/topic/%s.outgoing-call"), CALL_INVITATION("/topic/%s.incoming-call");
	
	private String path;

	private Destinations(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return this.path;
	}
}
