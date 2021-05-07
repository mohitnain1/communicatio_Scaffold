package com.scaffold.chat.controller;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.datatransfer.ChatRoomCreationParams;
import com.scaffold.chat.datatransfer.ChatRoomResponse;
import com.scaffold.chat.datatransfer.ChatRoomUpdateParams;
import com.scaffold.chat.service.ChatRoomService;
import com.scaffold.chat.web.UrlConstants;
import com.scaffold.security.domains.UserCredentials;
import com.scaffold.web.util.Response;

import io.swagger.annotations.Api;

@RestController
@Api(value = "Chat Room Controller")
public class ChatRoomController {
	
	@Autowired public ChatRoomService chatRoomServices;
	
	@PostMapping(UrlConstants.CREATE_CHATROOM)
	public ResponseEntity<Object> createChatRoom(@RequestBody ChatRoomCreationParams params) {
		return chatRoomServices.createChatRoom(params.getChatRoomName(), params.getMembers());
	}
	
	@PutMapping(UrlConstants.UPDATE_MEMBERS)
	public ResponseEntity<Object> addMembers(@RequestBody ChatRoomUpdateParams params) {
		List<UserCredentials> members = chatRoomServices.addMembers(params);
		if(!members.isEmpty()) {
			return Response.generateResponse(HttpStatus.CREATED, members, "Added or updated members", true);
		}
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, "Please check the details", null, false);
	}
		
	@GetMapping(UrlConstants.GET_USER_ROOM)
	public ResponseEntity<Object> getUserChatRooms(@RequestParam long userId) {
		List<ChatRoomResponse> userChatRooms = chatRoomServices.userChatRooms(userId);
		if(Objects.isNull(userChatRooms)) {
			return Response.generateResponse(HttpStatus.OK, Collections.emptyList(), "No Chatroom Found", false);
		}
		else if(userChatRooms.isEmpty()) {
			return Response.generateResponse(HttpStatus.OK, Collections.emptyList(), "No Chatroom found", false);
		} else {
			return Response.generateResponse(HttpStatus.OK, userChatRooms, "Successful", true);
		}
	}
	
//	@PutMapping(UrlConstants.REMOVE_CHATROOM_MEMBERS)
//	public ResponseEntity<Object> removeChatRoomMembers(@RequestBody ChatRoomUpdateParams params) {
//		List<UserCredentials> removeMembers = chatRoomServices.removeMembers(params.getChatRoomId(), params.getMembers());
//		if(!removeMembers.isEmpty()) {
//			return Response.generateResponse(HttpStatus.OK, removeMembers, "Members Removed", true);
//		} else {
//			return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, "Unable to remove members.", false);
//		}
//	}
}
