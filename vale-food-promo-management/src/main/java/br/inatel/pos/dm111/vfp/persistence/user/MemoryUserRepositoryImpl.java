package br.inatel.pos.dm111.vfp.persistence.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class MemoryUserRepositoryImpl implements UserRepository
{
	private Map<String, User> db = new HashMap<>();

	@Override
	public List<User> getAll()
	{
		return db.values().stream().toList();
	}

	@Override
	public Optional<User> getById(String id)
	{
		return Optional.ofNullable(db.get(id));
	}

	@Override
	public Optional<User> getByEmail(String email)
	{
		return db.entrySet().stream().map(Map.Entry::getValue).filter(user -> user.email().equals(email)).findAny();
	}

	@Override
	public User save(User user)
	{
		return db.put(user.id(), user);
	}

	@Override
	public void delete(String id)
	{
		db.values().removeIf(user -> user.id().equals(id));
	}
}