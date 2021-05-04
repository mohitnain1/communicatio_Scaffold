package com.scaffold.chat.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.scaffold.chat.datatransfer.FileUploadParms;
@Service
public interface ChatFileUploadService {
	
	public Map<String, Object> uploadFile(FileUploadParms fileParms, HttpServletRequest request);
	public byte[] downloadFile(String fileName);
	public Object deleteFile(String fileName);
	
}
