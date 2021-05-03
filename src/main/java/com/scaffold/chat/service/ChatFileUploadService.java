package com.scaffold.chat.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.scaffold.chat.datatransfer.FileUploadParms;
@Service
public interface ChatFileUploadService {
	public String uploadFile(FileUploadParms fileParms, HttpServletRequest request);
	public byte[] downloadFile(String fileName);
	public Object deleteFile(String fileName);
	
}
