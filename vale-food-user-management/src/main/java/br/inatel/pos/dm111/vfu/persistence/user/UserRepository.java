package br.inatel.pos.dm111.vfu.persistence.user;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import br.inatel.pos.dm111.vfu.persistence.ValeFoodRepository;

public interface UserRepository extends ValeFoodRepository<User>
{
	Optional<User> getByEmail(String email) throws InterruptedException, ExecutionException;
}
