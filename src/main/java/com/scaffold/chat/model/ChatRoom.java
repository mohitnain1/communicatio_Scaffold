package com.scaffold.chat.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "chatRoom")
public class ChatRoom {
	@Id
	private String id;
	private String chatRoomId;
	private String chatRoomName;
	private String chatRoomType;
	private LocalDateTime chatRoomCreationDate;
	private LocalDateTime chatRoomLastConversationDate;
	
	private String roomAccessKey;
	private List<Member> members;
	
	@DBRef(lazy = true)
	private MessageStore messageStore;
	
	public ChatRoom() {}
	
	public ChatRoom(String chatRoomName, List<Member> members) {
		this.chatRoomName = chatRoomName;
		this.members = members;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getChatRoomType() {
		return chatRoomType;
	}

	public void setChatRoomType(String chatRoomType) {
		this.chatRoomType = chatRoomType;
	}

	public LocalDateTime getChatRoomCreationDate() {
		return chatRoomCreationDate;
	}

	public void setChatRoomCreationDate(LocalDateTime chatRoomCreationDate) {
		this.chatRoomCreationDate = chatRoomCreationDate;
	}

	public LocalDateTime getChatRoomLastConversationDate() {
		return chatRoomLastConversationDate;
	}

	public void setChatRoomLastConversationDate(LocalDateTime chatRoomLastConversationDate) {
		this.chatRoomLastConversationDate = chatRoomLastConversationDate;
	}

	public String getRoomAccessKey() {
		return roomAccessKey;
	}

	public void setRoomAccessKey(String roomAccessKey) {
		this.roomAccessKey = roomAccessKey;
	}

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

	public MessageStore getMessageStore() {
		return messageStore;
	}

	public void setMessageStore(MessageStore messageStore) {
		this.messageStore = messageStore;
	}
}
