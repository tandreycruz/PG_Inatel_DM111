package br.inatel.pos.dm111.vfu.persistence;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface ValeFoodRepository<T>
{
	List<T> getAll() throws ExecutionException, InterruptedException;

	Optional<T> getById(String id) throws InterruptedException, ExecutionException;

	//Optional<T> getByUserId(String userId) throws InterruptedException, ExecutionException;

	T save(T entity);

	void delete(String id) throws InterruptedException, ExecutionException;
}
