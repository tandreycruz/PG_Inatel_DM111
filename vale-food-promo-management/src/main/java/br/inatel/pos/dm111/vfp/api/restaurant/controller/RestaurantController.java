package br.inatel.pos.dm111.vfp.api.restaurant.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.inatel.pos.dm111.vfp.api.restaurant.RestaurantRequest;
import br.inatel.pos.dm111.vfp.api.restaurant.service.RestaurantService;

@RestController
@RequestMapping("/valefood/restaurants")
public class RestaurantController
{
	private static final Logger log = LoggerFactory.getLogger(RestaurantController.class);
	
	private final RestaurantService service;
	
	public RestaurantController(RestaurantService service)
	{
		this.service = service;
	}
	
	@PostMapping
	public ResponseEntity<RestaurantRequest> postRestaurant(@RequestBody RestaurantRequest request)
	{
		log.debug("Received request to create a new restaurant into the cache...");
		
		var response = service.createRestaurant(request);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
