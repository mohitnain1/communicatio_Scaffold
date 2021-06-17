package com.scaffold.chat.domains;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SignalPayload {
	private long senderId;
	private String destination;
	private long userToSignalId;
	private String signal;
	private String contentType;
	
}
