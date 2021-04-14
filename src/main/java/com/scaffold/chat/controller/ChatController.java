package com.scaffold.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.service.ChatRoomService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "Chat Room Controller")
@RequestMapping("/chat")
public class ChatController {
	
	@Autowired public ChatRoomService chatRoomServices;
	
	//chatRoom creation....
	@ApiOperation(value = "Create chatroom", notes = "This api is used to create chatroom.")
	@PostMapping(value = "/create-chatRoom")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> createChatroom(@RequestParam String chatRoomName, 
			@RequestParam long chatRoomCreatorId, @RequestParam List<Long> chatRoomMemebersId ) {
		return new ResponseEntity<>(chatRoomServices.createChatRoom(chatRoomName, chatRoomCreatorId, chatRoomMemebersId), HttpStatus.OK);
	}
	
	//Add members in chatRoom....
	@PutMapping(value = "/addUsers")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> addMembersInChatRoom(@RequestParam String chatRoomId, 
			@RequestParam List<Long> chatRoomMemebersId ) {
		return new ResponseEntity<>(chatRoomServices.addMembers(chatRoomId, chatRoomMemebersId), HttpStatus.OK);
	}
	
	//Remove members with chatRoom....
		@PutMapping(value = "/removeUsers")
		@ResponseStatus(HttpStatus.OK)
		public ResponseEntity<Object> removeMembersWithChatRoom(@RequestParam String chatRoomId, 
				@RequestParam List<Long> chatRoomMemebersId ) {
			return new ResponseEntity<>(chatRoomServices.removeMembers(chatRoomId, chatRoomMemebersId), HttpStatus.OK);
		}
	
	
}
