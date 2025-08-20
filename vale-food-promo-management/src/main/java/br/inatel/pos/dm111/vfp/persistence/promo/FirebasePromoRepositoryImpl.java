package br.inatel.pos.dm111.vfp.persistence.promo;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.google.cloud.firestore.Firestore;

@Profile("local")
@Component
public class FirebasePromoRepositoryImpl implements PromoRepository
{
private static final String COLLECTION_NAME = "promos";
	
	private final Firestore firestore;
	
	public FirebasePromoRepositoryImpl(Firestore firestore)
	{
		this.firestore = firestore;
	}

	@Override
	public List<Promo> getAll() throws ExecutionException, InterruptedException
	{
		return firestore.collection(COLLECTION_NAME).get().get().getDocuments().parallelStream().map(doc -> doc.toObject(Promo.class)).toList();
	}
	
	@Override
	public Optional<Promo> getById(String id) throws InterruptedException, ExecutionException
	{
		var promo = firestore.collection(COLLECTION_NAME).document(id).get().get().toObject(Promo.class);
		return Optional.ofNullable(promo);
	}

	@Override
	public List<Promo> getAllByPreferencesUser(String userId) throws InterruptedException, ExecutionException
	{
		//return firestore.collection(COLLECTION_NAME).get().get().getDocuments().stream().map(doc -> doc.toObject(Promo.class)).filter(restaurant -> restaurant.userId().equalsIgnoreCase(userId)).findFirst();
		return null;
	}

	@Override
	public Promo save(Promo promo)
	{
		firestore.collection(COLLECTION_NAME).document(promo.id()).set(promo);
		return promo;
	}

	@Override
	public void delete(String id) throws InterruptedException, ExecutionException
	{
		firestore.collection(COLLECTION_NAME).document(id).delete().get();		
	}
}
