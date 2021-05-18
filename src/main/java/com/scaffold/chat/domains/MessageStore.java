package com.scaffold.chat.domains;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.Data;

public @Data class MessageStore {
	
	@Id
	private String id;
	@Indexed
	private String chatRoomId;
	private List<Message> messageDetails = new LinkedList<>();
	boolean isDeleted = false;
}
