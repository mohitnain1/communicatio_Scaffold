package com.scaffold.chat.web;

public class UrlConstants {
	
	private static final String PREFIX = "/chat";
	
	public static final String USER_INVITE_SUBSCRIPTION = "/topic.%s.invitations";
	public static final String USER_CONNECT_EVENT = "/topic.%s.userlogin";
	public static final String USER_DISCONNECT_EVENT = "/topic.%s.userdisconnect";
	public static final String USER_CHATROOM_SUBSCRIPTION = "/topic.%s.userlogin";
	public static final String GET_USER_ROOM = "/user/chatrooms";
}
