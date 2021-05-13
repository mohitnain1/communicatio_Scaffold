package com.scaffold.chat.datatransfer;

public class ChatRoomUpdateParams {
	
	private String chatRoomId;
	private long senderId;
	private MemberAddOrRemoveRequest members;
	
	public String getChatRoomId() {
		return chatRoomId;
	}
	public void setChatRoomId(String chatRoomId) {
		this.chatRoomId = chatRoomId;
	}
	public long getSenderId() {
		return senderId;
	}
	public void setSenderId(long senderId) {
		this.senderId = senderId;
	}
	public MemberAddOrRemoveRequest getMembers() {
		return members;
	}
	public void setMembers(MemberAddOrRemoveRequest members) {
		this.members = members;
	}
	
	
	
}