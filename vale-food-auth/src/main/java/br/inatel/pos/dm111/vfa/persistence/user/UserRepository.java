package br.inatel.pos.dm111.vfa.persistence.user;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import br.inatel.pos.dm111.vfa.persistence.ValeFoodRepository;

public interface UserRepository extends ValeFoodRepository<User>
{
	Optional<User> getByEmail(String email) throws InterruptedException, ExecutionException;
}
