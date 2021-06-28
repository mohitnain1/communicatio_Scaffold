package com.scaffold.chat.domains;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
public @Data class ChatRoom {
	
	@Id
	private String id;
	@Indexed(unique = true)
	private String chatRoomId;
	private String chatRoomName;
	private LocalDateTime creationDate;
	private LocalDateTime lastConversation;
	private String roomAccessKey;
	private List<Member> members;
	private boolean isDeleted = false;
	private boolean isCallActive = false;
	
	@DBRef(lazy = true)
	private MessageStore messageStore;
	
	public ChatRoom() {}
	
	public ChatRoom(String chatRoomName, List<Member> members) {
		this.chatRoomName = chatRoomName;
		this.members = members;
	}
}
