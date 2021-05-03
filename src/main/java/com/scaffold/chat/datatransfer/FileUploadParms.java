package com.scaffold.chat.datatransfer;

public class FileUploadParms {
	
	private long senderId;
	private String chatRoomId;
	private String fileName;
	private String fileData; //data
	
	public long getSenderId() {
		return senderId;
	}
	public void setSenderId(long senderId) {
		this.senderId = senderId;
	}
	public String getChatRoomId() {
		return chatRoomId;
	}
	public void setChatRoomId(String chatRoomId) {
		this.chatRoomId = chatRoomId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileData() {
		return fileData;
	}
	public void setFileData(String fileData) {
		this.fileData = fileData;
	}
	
	@Override
	public String toString() {
		return "FileUploadParms [senderId=" + senderId + ", chatRoomId=" + chatRoomId + ", fileName=" + fileName
				+ ", fileData=" + fileData + "]";
	}
	
	
	
}
