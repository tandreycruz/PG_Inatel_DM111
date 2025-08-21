package br.inatel.pos.dm111.vfu.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import br.inatel.pos.dm111.vfu.persistence.user.User;

@Profile("test")
@Component
public class UserHttpPublisher implements AppPublisher
{
	@Value("${vale-food.restaurant.url}")
	private String restaurantUrl;
	
	@Value("${vale-food.auth.url}")
	private String authUrl;
	
	@Value("${vale-food.promo.url}")
	private String promoUrl;
	
	private final RestTemplate restTemplate;
	
	public UserHttpPublisher(RestTemplate restTemplate)
	{
		this.restTemplate = restTemplate;
	}

	@Override
	public boolean publishCreated(User user)
	{
		var event = buildEvent(user, Event.EventType.ADDED);
		
		restTemplate.postForObject(restaurantUrl, event.event(), UserEvent.class);
		restTemplate.postForObject(authUrl, event.event(), UserEvent.class);
		restTemplate.postForObject(promoUrl, event.event(), UserEvent.class);
		
		return true;
	}
	
	@Override
	public boolean publishUpdated(User user)
	{
	    var event = buildEvent(user, Event.EventType.UPDATED);

	    restTemplate.put(restaurantUrl + "/" + user.id(), event.event());
	    restTemplate.put(authUrl + "/" + user.id(), event.event());
	    restTemplate.put(promoUrl + "/" + user.id(), event.event());

	    return true;
	}
	
	@Override
	public boolean publishDeleted(String id)
	{
	    restTemplate.delete(restaurantUrl + "/" + id);
	    restTemplate.delete(authUrl + "/" + id);
	    restTemplate.delete(promoUrl + "/" + id);

	    return true;
	}
}
