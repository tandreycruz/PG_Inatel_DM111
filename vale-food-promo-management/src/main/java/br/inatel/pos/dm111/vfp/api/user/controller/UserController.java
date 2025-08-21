package br.inatel.pos.dm111.vfp.api.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.remoteconfig.internal.TemplateResponse.UserResponse;

import br.inatel.pos.dm111.vfp.api.core.ApiException;
import br.inatel.pos.dm111.vfp.api.user.UserRequest;
import br.inatel.pos.dm111.vfp.api.user.service.UserService;

@RestController
@RequestMapping("/valefood/users")
public class UserController
{
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	private final UserService service;
			
	public UserController(UserService service)
	{
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<UserRequest> postUser(@RequestBody UserRequest request)
	{
		log.debug("Received request to create a new user into the cache...");
		
		var response = service.createUser(request);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PutMapping(value = "/{userId}")
	public ResponseEntity<UserRequest> putUser(@RequestBody UserRequest request, @PathVariable("userId") String userId) throws ApiException
	{
		log.debug("Received request to update an user...");
		
		var response = service.updateUser(request, userId);
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@DeleteMapping(value = "/{userId}")
	public ResponseEntity<UserResponse> deleteUser(@PathVariable("userId") String id) throws ApiException
	{
		log.debug("Received request to delete an user: id {}", id);
		
		service.removeUser(id);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
