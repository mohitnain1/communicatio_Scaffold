package com.scaffold.chat.datatransfer;

import java.util.List;

import com.scaffold.web.util.FileData;

public class FileUploadParms {
	
	private long senderId;
	private String chatRoomId;
	private List<FileData> files;
	
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
	public List<FileData> getFiles() {
		return files;
	}
	public void setFiles(List<FileData> files) {
		this.files = files;
	}
	@Override
	public String toString() {
		return "FileUploadParms [senderId=" + senderId + ", chatRoomId=" + chatRoomId + ", files=" + files + "]";
	}
}
