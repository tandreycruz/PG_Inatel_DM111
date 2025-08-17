package br.inatel.pos.dm111.vfu.persistence.restaurant;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import br.inatel.pos.dm111.vfu.persistence.ValeFoodRepository;

public interface RestaurantRepository extends ValeFoodRepository<Restaurant>
{
	Optional<Restaurant> getByUserId(String userId) throws InterruptedException, ExecutionException;
}
