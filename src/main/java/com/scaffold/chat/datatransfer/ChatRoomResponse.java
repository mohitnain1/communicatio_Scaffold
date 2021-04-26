package com.scaffold.chat.datatransfer;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatRoomResponse {

	private String chatRoomId;
	private String chatRoomName;
	private String roomAccessKey;
	private List<Long> chatRoomMembersId;
	private long chatRoomCreatorId;

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

	public List<Long> getChatRoomMembersId() {
		return chatRoomMembersId;
	}

	public void setChatRoomMembersId(List<Long> chatRoomMembersId) {
		this.chatRoomMembersId = chatRoomMembersId;
	}

	public long getChatRoomCreatorId() {
		return chatRoomCreatorId;
	}

	public void setChatRoomCreatorId(long chatRoomCreatorId) {
		this.chatRoomCreatorId = chatRoomCreatorId;
	}

}
