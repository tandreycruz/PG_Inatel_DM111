package br.inatel.pos.dm111.vfa.persistence.user;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.google.cloud.firestore.Firestore;

@Profile("local")
@Component
public class FirebaseUserRepositoryImpl implements UserRepository
{
	private static final String COLLECTION_NAME = "users";
	
	private final Firestore firestore;
	
	public FirebaseUserRepositoryImpl(Firestore firestore)
	{
		this.firestore = firestore;
	}

	@Override
	public List<User> getAll() throws ExecutionException, InterruptedException
	{
		return firestore.collection(COLLECTION_NAME).get().get().getDocuments().parallelStream().map(doc -> doc.toObject(User.class)).toList();
	}

	@Override
	public Optional<User> getById(String id) throws InterruptedException, ExecutionException
	{
		var user = firestore.collection(COLLECTION_NAME).document(id).get().get().toObject(User.class);
		return Optional.ofNullable(user);
	}

	@Override
	public Optional<User> getByEmail(String email) throws InterruptedException, ExecutionException
	{
		return firestore.collection(COLLECTION_NAME).get().get().getDocuments().stream().map(doc -> doc.toObject(User.class)).filter(user -> user.email().equalsIgnoreCase(email)).findFirst();
	}

	@Override
	public User save(User user)
	{
		firestore.collection(COLLECTION_NAME).document(user.id()).set(user);
		return user;
	}

	@Override
	public void delete(String id) throws InterruptedException, ExecutionException
	{
		firestore.collection(COLLECTION_NAME).document(id).delete().get();		
	}
}
