package com.scaffold.chat.web;

public class UrlConstants {
	
	private static final String PREFIX = "/chat";
	
	/**
	 * Destination Constants
	 */
	public static final String USER_INVITE_SUBSCRIPTION = "/topic.%s.invitations";
	public static final String USER_CONNECT_EVENT = "/topic.%s.userlogin";
	public static final String USER_DISCONNECT_EVENT = "/topic.%s.userdisconnect";
	
	/**
	 * ChatRoom Constants.
	 */
	public static final String GET_USER_ROOM = PREFIX + "/user/chatrooms";
	public static final String UPDATE_MEMBERS = PREFIX + "/members";
	public static final String CREATE_CHATROOM = PREFIX +"/chatRoom";
	public static final String DELETE_CHATROOM = PREFIX +"/chatRoom";
	public static final String REMOVE_CHATROOM_MEMBERS = PREFIX +"/chatRoom/removeMembers";
	
	public static final String GET_ALL_MESSAGES = PREFIX +"/messages";
	
	
	public static final String FILE_UPLOAD  = PREFIX +"/upload";
	public static final String FILE_DOWNLOAD  = PREFIX +"/download/{fileName}";
	public static final String FILE_DELETE  = PREFIX +"/delete/{fileName}";
	
}
