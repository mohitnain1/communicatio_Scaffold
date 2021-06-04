package com.scaffold.chat.service;

import org.springframework.stereotype.Service;

import com.scaffold.chat.datatransfer.VideoCallParams;

@Service
public interface VideoCallService {

	public Object startCall(VideoCallParams params);

}
