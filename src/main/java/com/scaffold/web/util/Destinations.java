package com.scaffold.web.util;

public enum Destinations {
	CHATROOM_JOIN("/queue.%s.userJoined"), CHATROOM_LEFT("/queue.%s.userLeft"), 
	INVITATION("/topic/%s/invitations"), MESSGE_EVENT_NOTIFICATION("/topic/%s/message-notifiation");
	
	private String path;

	private Destinations(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return this.path;
	}
}
