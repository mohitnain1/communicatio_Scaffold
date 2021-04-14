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
	
	private long chatRoomCreatorId;
	private List<Long> chatRoomMemebersId;
	
	@DBRef(lazy = true)
	private MessageStore messageStore;
	
	public ChatRoom() {}
	
	public ChatRoom(String chatRoomName, long chatRoomCreatorId, List<Long> chatRoomMemebersId) {
		this.chatRoomName = chatRoomName;
		this.chatRoomCreatorId = chatRoomCreatorId;
		this.chatRoomMemebersId = chatRoomMemebersId;
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

	public long getChatRoomCreatorId() {
		return chatRoomCreatorId;
	}

	public void setChatRoomCreatorId(long chatRoomCreatorId) {
		this.chatRoomCreatorId = chatRoomCreatorId;
	}

	public List<Long> getChatRoomMemebersId() {
		return chatRoomMemebersId;
	}

	public void setChatRoomMemebersId(List<Long> chatRoomMemebersId) {
		this.chatRoomMemebersId = chatRoomMemebersId;
	}

	public MessageStore getMessageStore() {
		return messageStore;
	}

	public void setMessageStore(MessageStore messageStore) {
		this.messageStore = messageStore;
	}
	
}