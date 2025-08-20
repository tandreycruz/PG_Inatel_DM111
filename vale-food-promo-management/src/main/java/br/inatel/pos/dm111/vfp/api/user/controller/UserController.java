package br.inatel.pos.dm111.vfp.api.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
