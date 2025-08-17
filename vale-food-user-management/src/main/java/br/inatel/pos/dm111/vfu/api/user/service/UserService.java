package br.inatel.pos.dm111.vfu.api.user.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.inatel.pos.dm111.vfu.api.PasswordEncryptor;
import br.inatel.pos.dm111.vfu.api.core.ApiException;
import br.inatel.pos.dm111.vfu.api.core.AppErrorCode;
import br.inatel.pos.dm111.vfu.api.user.UserRequest;
import br.inatel.pos.dm111.vfu.api.user.UserResponse;
import br.inatel.pos.dm111.vfu.persistence.user.User;
import br.inatel.pos.dm111.vfu.persistence.user.UserRepository;

@Service
public class UserService
{
	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository repository;
	private final PasswordEncryptor encryptor;

	public UserService(UserRepository repository, PasswordEncryptor encryptor)
	{
		this.repository = repository;
		this.encryptor = encryptor;
	}

	public List<UserResponse> searchUsers() throws ApiException
	{
		return retrieveUsers().stream().map(this::buildUserResponse).toList();
	}
	
	public UserResponse searchUser(String id) throws ApiException
	{
		return retrieveUserById(id).map(this::buildUserResponse).orElseThrow(() -> {
			log.warn("User was not found. Id: {}", id);
			return new ApiException(AppErrorCode.USER_NOT_FOUND);
		});
	}
	
	public UserResponse createUser(UserRequest request) throws ApiException
	{
		// uniqueness by email
		validateUser(request);
		
		var user = buildUser(request);
		repository.save(user);
		
		log.info("User was sucessfully created. Id: {}", user.id());
		
		return buildUserResponse(user);
	}
	
	public UserResponse updateUser(UserRequest request, String id) throws ApiException
	{
		// check user by id exist
		var userOpt = retrieveUserById(id);
		if (userOpt.isEmpty())
		{
			log.warn("User was not found. Id: {}", id);
			throw new ApiException(AppErrorCode.USER_NOT_FOUND);
		}
		else
		{
			var user = userOpt.get();
			if (request.email() != null && !user.email().equals(request.email()))
			{
				validateUser(request);
			}
		}
		
		var user = buildUser(request, id);
		repository.save(user);
		return buildUserResponse(user);
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

	private void validateUser(UserRequest request) throws ApiException
	{
		var userOpt = retrieveUserByEmail(request.email());
		if (userOpt.isPresent())
		{
			log.warn("Provided email already in use.");
			throw new ApiException(AppErrorCode.CONFLICTED_USER_EMAIL);
		}
	}	
	
	private User buildUser(UserRequest request)
	{
		var encryptedPwd = encryptor.encrypt(request.password());
		var userId = UUID.randomUUID().toString();
		return new User(userId, request.name(), request.email(), encryptedPwd, User.UserType.valueOf(request.type()));
	}

	private User buildUser(UserRequest request, String id)
	{
		var encryptedPwd = encryptor.encrypt(request.password());
		return new User(id, request.name(), request.email(), encryptedPwd, User.UserType.valueOf(request.type()));
	}

	private UserResponse buildUserResponse(User user)
	{
		return new UserResponse(user.id(), user.name(), user.email(),  user.type().name());
	}

	private List<User> retrieveUsers() throws ApiException
	{
		try
		{
			return repository.getAll();
		}
		catch (ExecutionException | InterruptedException e)
		{
			log.error("Failed to read all users from DB.", e);
			throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
		}
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
	
	private Optional<User> retrieveUserByEmail(String email) throws ApiException
	{
		try
		{
			return repository.getByEmail(email);
		}
		catch (InterruptedException | ExecutionException e)
		{
			log.error("Failed to read an users from DB by email.", e);
			throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
		}
	}
}
