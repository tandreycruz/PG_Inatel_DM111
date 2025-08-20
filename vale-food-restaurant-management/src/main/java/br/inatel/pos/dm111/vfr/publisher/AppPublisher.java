package br.inatel.pos.dm111.vfr.publisher;

import br.inatel.pos.dm111.vfr.persistence.restaurant.Restaurant;

public interface AppPublisher
{
	default Event buildEvent(Restaurant restaurant, Event.EventType eventType)
	{
		var restaurantEvent = buildRestaurantEvent(restaurant);
		return new Event(eventType, restaurantEvent);
	}
	
	default RestaurantEvent buildRestaurantEvent(Restaurant restaurant)
	{
		return new RestaurantEvent(restaurant.id(), restaurant.name(), restaurant.address(), restaurant.userId(), restaurant.categories(), restaurant.products());
	}
	
	boolean publishCreated(Restaurant restaurant);
}
