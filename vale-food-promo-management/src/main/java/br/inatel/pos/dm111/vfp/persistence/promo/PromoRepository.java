package br.inatel.pos.dm111.vfp.persistence.promo;

import java.util.List;
import java.util.concurrent.ExecutionException;

import br.inatel.pos.dm111.vfp.persistence.ValeFoodRepository;

public interface PromoRepository extends ValeFoodRepository<Promo>
{
	List<Promo> getAllByPreferencesUser(String userId) throws ExecutionException, InterruptedException;
}