package com.scaffold.chat.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.service.VideoCallService;
import com.scaffold.chat.web.UrlConstants;
import com.scaffold.web.util.Response;

@RestController
public class VideoCallController {
	@Autowired VideoCallService videoCallService;

	@PostMapping(UrlConstants.START_VIDEO_CALL)
	public ResponseEntity<Object> startVideoCall(@RequestHeader("Authorization") String accessToken, String chatRoomId) {
		Object callData = videoCallService.startCall(chatRoomId);
		if (Objects.nonNull(callData)) {
			return Response.generateResponse(HttpStatus.OK, callData, "Call Started.", true);
		}
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, "Please check the chatRoomId.", null, false);
	}
}
