package com.scaffold.chat.controller;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.datatransfer.ChatRoomCreationParams;
import com.scaffold.chat.datatransfer.ChatRoomResponse;
import com.scaffold.chat.datatransfer.ChatRoomUpdateParams;
import com.scaffold.chat.datatransfer.UserDataTransfer;
import com.scaffold.chat.service.ChatRoomService;
import com.scaffold.chat.web.UrlConstants;
import com.scaffold.web.util.Response;

import io.swagger.annotations.Api;

@RestController
@Api(value = "Chat Room Controller")
public class ChatRoomController {
	
	@Autowired public ChatRoomService chatRoomServices;
	
	@PostMapping(UrlConstants.CREATE_CHATROOM)
	public ResponseEntity<Object> createChatRoom(@RequestBody ChatRoomCreationParams params,
			@RequestHeader("Authorization") String accessToken) {
		try {
			return chatRoomServices.createChatRoom(params.getChatRoomName(), params.getMembers());
		} catch (Exception e) {
			return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, e.getLocalizedMessage(), false);
		}
	}
	
	@PutMapping(UrlConstants.UPDATE_MEMBERS)
	public ResponseEntity<Object> updateMembers(@RequestBody ChatRoomUpdateParams params, @RequestHeader("Authorization") String accessToken) {
		List<UserDataTransfer> members = chatRoomServices.updateUserInChatRoom(params);
		if(!members.isEmpty()) {
			return Response.generateResponse(HttpStatus.CREATED, members, "Added or updated members", true);
		}
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, "Please check the details", null, false);
	}
	
	@PreAuthorize("hasRole('ROLE_SUPERADMIN')")
	@DeleteMapping(UrlConstants.DELETE_CHATROOM)
	public ResponseEntity<Object> deleteChatRoom(@RequestParam String chatRoomId, @RequestHeader("Authorization")
	String accessToken) {
		boolean deleted = chatRoomServices.deleteChatRoom(chatRoomId);
		if(deleted) {
			return Response.generateResponse(HttpStatus.OK, "", "Deleted chatroom", true);
		}
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, "Unable to delete room. Please review details", null, false);
	}
		
	@GetMapping(UrlConstants.GET_USER_ROOM)
	public ResponseEntity<Object> getUserChatRooms(@RequestParam long userId, @RequestHeader("Authorization") String accessToken) {
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
	
}
