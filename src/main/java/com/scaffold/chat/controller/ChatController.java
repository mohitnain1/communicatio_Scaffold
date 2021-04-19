package com.scaffold.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.service.ChatRoomService;

import io.swagger.annotations.Api;

@RestController
@Api(value = "Chat Room Controller")
@RequestMapping("/chat")
public class ChatController {
	
	@Autowired public ChatRoomService chatRoomServices;
	
	//chatRoom creation....
	//@ApiOperation(value = "Create chatroom", notes = "This api is used to create chatroom.")
	@PostMapping(value = "/chatRoom")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> createChatRoom(@RequestParam String chatRoomName, 
			@RequestParam long creatorId, @RequestParam List<Long> membersId ) {
		try {
			String chatRoomId = chatRoomServices.createChatRoom(chatRoomName, creatorId, membersId);
			return new ResponseEntity<>(chatRoomId, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(chatRoomName +" Chatroom not created...", HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
	

	
}
