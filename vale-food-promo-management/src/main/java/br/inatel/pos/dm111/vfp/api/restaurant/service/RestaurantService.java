package br.inatel.pos.dm111.vfp.api.restaurant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.inatel.pos.dm111.vfp.api.restaurant.ProductRequest;
import br.inatel.pos.dm111.vfp.api.restaurant.RestaurantRequest;
import br.inatel.pos.dm111.vfp.persistence.restaurant.Product;
import br.inatel.pos.dm111.vfp.persistence.restaurant.Restaurant;
import br.inatel.pos.dm111.vfp.persistence.restaurant.RestaurantRepository;

@Service
public class RestaurantService
{
	private static final Logger log = LoggerFactory.getLogger(RestaurantService.class);

	private final RestaurantRepository restaurantRepository;
	
	public RestaurantService(RestaurantRepository restaurantRepository)
	{
		this.restaurantRepository = restaurantRepository;
	}
	
	public RestaurantRequest createRestaurant(RestaurantRequest request)
	{
		var restaurant = buildRestaurant(request);
		restaurantRepository.save(restaurant);
		
		log.info("Restaurant was sucessfully created. Id: {}", restaurant.id());
		
		return request;
	}
	
	private Restaurant buildRestaurant(RestaurantRequest request)
	{
		var products = request.products().stream().map(this::buildProduct).toList();
		
		return new Restaurant(request.id(), request.name(), request.address(), request.userId(), request.categories(), products);
	}
	
	private Product buildProduct(ProductRequest request)
	{
		return new Product(request.id(), request.name(), request.description(), request.category(), request.price());
	}
}
