package br.inatel.pos.dm111.vfp.persistence.promo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class MemoryPromoRepositoryImpl implements PromoRepository
{
	private Map<String, Promo> db = new HashMap<>();

	@Override
	public List<Promo> getAll()
	{
		return db.values().stream().toList();
	}

	@Override
	public Optional<Promo> getById(String id)
	{
		return Optional.ofNullable(db.get(id));
	}

	@Override
	public List<Promo> getAllByPreferencesUser(String userId)
	{
		//return db.entrySet().stream().map(Map.Entry::getValue).filter(restaurant -> restaurant.userId().equals(userId)).findAny();
		return null;
	}

	@Override
	public Promo save(Promo promo)
	{
		return db.put(promo.id(), promo);
	}

	@Override
	public void delete(String id)
	{
		db.values().removeIf(promo -> promo.id().equals(id));
	}
}
