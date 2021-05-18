package com.scaffold.chat.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.datatransfer.UserDTO;
import com.scaffold.chat.domains.User;
import com.scaffold.chat.service.UserService;
import com.scaffold.web.util.Response;

@RestController
public class UserController {
	
	@Autowired UserService userService;
	
	@GetMapping("/chat/users")
	public ResponseEntity<Object> getAllUsers(@RequestHeader("Authorization") String accessToken) {
		return Response.generateResponse(HttpStatus.OK, userService.getAllUsers(), "Success", true);
	}
	
	@PostMapping("/chat/users")
	public ResponseEntity<Object> saveUser(@RequestBody UserDTO user, @RequestHeader("Authorization") String accessToken) {
		User savedUser = userService.saveUser(user);
		if(Objects.nonNull(savedUser)) {
			return Response.generateResponse(HttpStatus.CREATED, savedUser, "Success", true);
		}
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, "Failed", false);
	}
	
	@PutMapping("/chat/users")
	public ResponseEntity<Object> updateUsers(@RequestBody UserDTO payload, @RequestParam String id, 
			@RequestHeader("Authorization") String accessToken) {
		User user = userService.updateUser(id, payload);
		if(Objects.nonNull(user)) {
			return Response.generateResponse(HttpStatus.CREATED, user, "Success", true);
		}
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, "Failed", false);
	}
	
	@DeleteMapping("/chat/users")
	public ResponseEntity<Object> deleteUser(@RequestParam String id, @RequestHeader("Authorization") String accessToken) {
		boolean deleted = userService.deleteUser(id);
		if(deleted) {
			return Response.generateResponse(HttpStatus.OK, deleted, "Success", true);
		}
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, "Failed", false); 
	}
	
	@GetMapping("/chat/users/{id}")
	public ResponseEntity<Object> getUserById(@RequestParam String id, @RequestHeader("Authorization") String accessToken) {
		User user = userService.getUser(id);
		if(Objects.nonNull(user)) {
			return Response.generateResponse(HttpStatus.OK, user, "Success", true);
		}
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, "Failed", false);
	}
}
