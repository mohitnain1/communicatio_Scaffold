package com.scaffold.chat.datatransfer;

public class ChatRoomUpdateParams {
	
	private String chatRoomId;
	private String updatorId;
	private MemberAddOrRemoveRequest members;
	
	public String getChatRoomId() {
		return chatRoomId;
	}
	public void setChatRoomId(String chatRoomId) {
		this.chatRoomId = chatRoomId;
	}
	public MemberAddOrRemoveRequest getMembers() {
		return members;
	}
	public void setMembers(MemberAddOrRemoveRequest members) {
		this.members = members;
	}
	public String getUpdatorId() {
		return updatorId;
	}
	public void setUpdatorId(String updatorId) {
		this.updatorId = updatorId;
	}
	
	
	
}