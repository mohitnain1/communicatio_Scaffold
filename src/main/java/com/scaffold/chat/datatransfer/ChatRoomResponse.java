package com.scaffold.chat.datatransfer;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scaffold.security.domains.UserCredentials;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatRoomResponse {

	private String chatRoomId;
	private String chatRoomName;
	private String roomAccessKey;
	private List<UserCredentials> members;
	private UserCredentials chatRoomCreator;
	public String getChatRoomId() {
		return chatRoomId;
	}
	public void setChatRoomId(String chatRoomId) {
		this.chatRoomId = chatRoomId;
	}
	public String getChatRoomName() {
		return chatRoomName;
	}
	public void setChatRoomName(String chatRoomName) {
		this.chatRoomName = chatRoomName;
	}
	public String getRoomAccessKey() {
		return roomAccessKey;
	}
	public void setRoomAccessKey(String roomAccessKey) {
		this.roomAccessKey = roomAccessKey;
	}
	public List<UserCredentials> getMembers() {
		return members;
	}
	public void setMembers(List<UserCredentials> members) {
		this.members = members;
	}
	public UserCredentials getChatRoomCreator() {
		return chatRoomCreator;
	}
	public void setChatRoomCreator(UserCredentials chatRoomCreator) {
		this.chatRoomCreator = chatRoomCreator;
	}
}
