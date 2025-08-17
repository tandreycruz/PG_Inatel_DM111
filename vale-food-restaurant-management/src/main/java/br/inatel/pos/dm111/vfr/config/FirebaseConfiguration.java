package br.inatel.pos.dm111.vfr.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Configuration
public class FirebaseConfiguration
{
	private static final String APP_NAME = "vale-food";
	
	@Value("classpath:service-account.json")
	Resource resource;
	
	@Bean
	public GoogleCredentials googleCredentials() throws IOException
	{
		return GoogleCredentials.fromStream(resource.getInputStream());
	}
	
	@Bean
	public FirebaseOptions firebaseOptions(GoogleCredentials credentials)
	{
		return FirebaseOptions.builder().setCredentials(credentials).build();
	}
	
	@Bean
	public FirebaseApp firebaseApp(FirebaseOptions firebaseOptions)
	{
		return FirebaseApp.initializeApp(firebaseOptions, APP_NAME);
	}
	
	@Bean
	public Firestore firestore(FirebaseApp firebaseApp)
	{
		return FirestoreClient.getFirestore(firebaseApp);
	}
}
