package com.scaffold.chat.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.datatransfer.ChatRoomCreationParams;
import com.scaffold.chat.service.ChatRoomService;
import com.scaffold.chat.web.UrlConstants;

import io.swagger.annotations.Api;

@RestController
@Api(value = "Chat Room Controller")
@RequestMapping("/chat")
public class ChatRoomController {
	
	@Autowired public ChatRoomService chatRoomServices;
	
	@PostMapping(value = "/chatRoom")
	public ResponseEntity<Object> createChatRoom(@RequestBody ChatRoomCreationParams params) {
		HashMap<String, Object> chatRoomId = chatRoomServices.createChatRoom(params.getChatRoomName(),
				params.getCreatorId(), params.getMembersId());
		return new ResponseEntity<>(chatRoomId, HttpStatus.CREATED);
	}
	
	//Add members in chatRoom....
	@PutMapping(value = "/members")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> addMembers(@RequestParam String chatRoomId,@RequestParam List<Long> membersId ) {
		try {
			List<Long> addMembersId = chatRoomServices.addMembers(chatRoomId, membersId);
			return new ResponseEntity<>(addMembersId, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(membersId+" membersId not added in chatRoom", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//Remove members with chatRoom....
	@DeleteMapping(value = "/members")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> removeMembers(@RequestParam String chatRoomId,@RequestParam List<Long> membersId) {
		try {
			List<Long> removedMembersId = chatRoomServices.removeMembers(chatRoomId, membersId);
			return new ResponseEntity<>(removedMembersId, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(membersId+" membersId not deleted with chatRoom", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(UrlConstants.GET_USER_ROOM)
	public ResponseEntity<Object> getUserChatRooms(@RequestParam long userId) {
		List<Map<String, Object>> userChatRooms = chatRoomServices.userChatRooms(userId);
		if(Objects.isNull(userChatRooms)) {
			return new ResponseEntity<Object>("Invalid User", HttpStatus.OK);
		} else if(userChatRooms.isEmpty()) {
			return new ResponseEntity<Object>(new ArrayList<>(), HttpStatus.OK);
		} else {
			return new ResponseEntity<Object>(userChatRooms, HttpStatus.OK);
		}
	}
}
