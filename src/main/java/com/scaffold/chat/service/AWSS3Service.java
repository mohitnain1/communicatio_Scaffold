package com.scaffold.chat.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
public interface AWSS3Service {
	public String uploadFile(MultipartFile multipartFile, HttpServletRequest request);
	public byte[] downloadFile(String fileName);
	public String deleteFile(String fileName);
}
