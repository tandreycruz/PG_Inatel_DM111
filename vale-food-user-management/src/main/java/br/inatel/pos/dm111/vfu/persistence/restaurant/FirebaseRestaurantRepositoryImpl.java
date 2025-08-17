package br.inatel.pos.dm111.vfu.persistence.restaurant;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.google.cloud.firestore.Firestore;

@Profile("local")
@Component
public class FirebaseRestaurantRepositoryImpl implements RestaurantRepository
{
	private static final String COLLECTION_NAME = "restaurants";
	
	private final Firestore firestore;
	
	public FirebaseRestaurantRepositoryImpl(Firestore firestore)
	{
		this.firestore = firestore;
	}

	@Override
	public List<Restaurant> getAll() throws ExecutionException, InterruptedException
	{
		return firestore.collection(COLLECTION_NAME).get().get().getDocuments().parallelStream().map(doc -> doc.toObject(Restaurant.class)).toList();
	}
	
	@Override
	public Optional<Restaurant> getById(String id) throws InterruptedException, ExecutionException
	{
		var restaurant = firestore.collection(COLLECTION_NAME).document(id).get().get().toObject(Restaurant.class);
		return Optional.ofNullable(restaurant);
	}

	@Override
	public Optional<Restaurant> getByUserId(String userId) throws InterruptedException, ExecutionException
	{
		return firestore.collection(COLLECTION_NAME).get().get().getDocuments().stream().map(doc -> doc.toObject(Restaurant.class)).filter(restaurant -> restaurant.userId().equalsIgnoreCase(userId)).findFirst();
	}

	@Override
	public Restaurant save(Restaurant restaurant)
	{
		firestore.collection(COLLECTION_NAME).document(restaurant.id()).set(restaurant);
		return restaurant;
	}

	@Override
	public void delete(String id) throws InterruptedException, ExecutionException
	{
		firestore.collection(COLLECTION_NAME).document(id).delete().get();		
	}
}
