package br.inatel.pos.dm111.vfr.api.user.service;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.inatel.pos.dm111.vfr.api.core.ApiException;
import br.inatel.pos.dm111.vfr.api.core.AppErrorCode;
import br.inatel.pos.dm111.vfr.api.user.UserRequest;
import br.inatel.pos.dm111.vfr.persistence.user.User;
import br.inatel.pos.dm111.vfr.persistence.user.UserRepository;

@Service
public class UserService
{
	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository repository;
																																
	public UserService(UserRepository repository)
	{
		this.repository = repository;
	}

	public UserRequest createUser(UserRequest request)
	{
		var user = buildUser(request);
		repository.save(user);
		
		log.info("User was sucessfully created. Id: {}", user.id());
		
		return request;
	}
	
	public UserRequest updateUser(UserRequest request, String id) throws ApiException
	{
		// check user by id exist
		var userOpt = retrieveUserById(id);
		if (userOpt.isEmpty())
		{
			log.warn("User was not found. Id: {}", id);
			throw new ApiException(AppErrorCode.USER_NOT_FOUND);
		}
		
		var user = buildUser(request, id);
		repository.save(user);
		
		log.info("User was sucessfully updated. Id: {}", user.id());
		
		return request;
	}
	
	public void removeUser(String id) throws ApiException
	{
		try
		{
			repository.delete(id);
			log.info("User was sucessfully deleted. id {}", id);
		}
		catch (InterruptedException | ExecutionException e)
		{
			log.error("Failed to delete an user from DB by id {}.", id, e);
			throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
		}
	}
	
	private User buildUser(UserRequest request)
	{
		return new User(request.id(), request.name(), request.email(), null, User.UserType.valueOf(request.type()), request.favoriteProducts());
	}
	
	private User buildUser(UserRequest request, String id)
	{
		return new User(id, request.name(), request.email(), null, User.UserType.valueOf(request.type()), request.favoriteProducts());
	}
	
	private Optional<User> retrieveUserById(String id) throws ApiException
	{
		try
		{
			return repository.getById(id);
		}
		catch (InterruptedException | ExecutionException e)
		{
			log.error("Failed to read an users from DB by id {}.", id, e);
			throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
		}
	}
}
