package br.inatel.pos.dm111.vfp.api.promo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.inatel.pos.dm111.vfp.api.core.ApiException;
import br.inatel.pos.dm111.vfp.api.core.AppErrorCode;
import br.inatel.pos.dm111.vfp.api.promo.PromoRequest;
import br.inatel.pos.dm111.vfp.api.promo.PromoResponse;
import br.inatel.pos.dm111.vfp.persistence.promo.Promo;
import br.inatel.pos.dm111.vfp.persistence.promo.PromoRepository;
import br.inatel.pos.dm111.vfp.persistence.restaurant.Product;
import br.inatel.pos.dm111.vfp.persistence.restaurant.Restaurant;
import br.inatel.pos.dm111.vfp.persistence.restaurant.RestaurantRepository;
import br.inatel.pos.dm111.vfp.persistence.user.User;
import br.inatel.pos.dm111.vfp.persistence.user.UserRepository;

@Service
public class PromoService
{
	private static final Logger log = LoggerFactory.getLogger(PromoService.class);

	private final PromoRepository promoRepository;
	
	private final RestaurantRepository restaurantRepository;
	
	private final UserRepository userRepository;

	public PromoService(PromoRepository promoRepository, RestaurantRepository restaurantRepository, UserRepository userRepository)
	{
		this.promoRepository = promoRepository;
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
	}
	
	
	public List<PromoResponse> searchPromos() throws ApiException
	{
		List<PromoResponse> responses = new ArrayList<PromoResponse>();
	    
		List<Promo> promos = retrievePromos();
		if (promos != null && promos.size() > 0)
	    {
			for (Promo promo : promos)
		    {
		        responses.add(buildPromoResponse(promo));
		    }
	    }
	    
		return responses;
	}
	
	public PromoResponse searchPromo(String id) throws ApiException
	{
		var promoOpt = retrievePromoById(id);
		if (promoOpt.isEmpty())
		{
			log.warn("Promotion was not found. Id: {}", id);
			throw new ApiException(AppErrorCode.PROMOTION_NOT_FOUND);
		}
		
		return buildPromoResponse(promoOpt.get());		
	}
	
	public List<PromoResponse> searchPromosFavoriteProductsByUser(String userId) throws ApiException
	{
		List<PromoResponse> responses = new ArrayList<PromoResponse>();
	    
		var userOpt = retrieveUserById(userId);
		if (userOpt.isEmpty())
		{
			log.warn("User was not found. Id: {}", userId);
			throw new ApiException(AppErrorCode.USER_NOT_FOUND);
		}
		
		var user = userOpt.get();
		List<Promo> promos = retrievePromos();
		if (promos != null && promos.size() > 0)
	    {
			for (Promo promo : promos)
		    {
				var restaurantOpt = retrieveRestaurantById(promo.restaurantId());
				if (!restaurantOpt.isEmpty())
				{
					var restaurant = restaurantOpt.get();
					if (restaurant.products() != null && restaurant.products().size() > 0)
					{
						boolean existsFavoriteProductOnPromo = false;
						for (Product product : restaurant.products())
						{
							if (product.id().equals(promo.productId()))
							{
								for (String favoriteProduct : user.favoriteProducts())
								{
									if (product.name().trim().equalsIgnoreCase(favoriteProduct.trim()))
									{
										existsFavoriteProductOnPromo = true;
										break;
									}
								}
							}
							if (existsFavoriteProductOnPromo)
							{
								break;
							}
						}
						if (existsFavoriteProductOnPromo)
						{
							responses.add(buildPromoResponse(promo));
						}
					}
				}
		    }
	    }
	    
		return responses;
	}
	
	public PromoResponse createPromo(PromoRequest request) throws ApiException
	{
		validatePromo(request);
		
		var promo = buildPromo(request);
		promoRepository.save(promo);
		
		log.info("Promotion was sucessfully created. Id: {}", promo.id());
		
		return buildPromoResponse(promo);
	}

	public PromoResponse updatePromo(PromoRequest request, String id) throws ApiException
	{
		// check promo by id exist
		var promoOpt = retrievePromoById(id);
		if (promoOpt.isEmpty())
		{
			log.warn("Promotion was not found. Id: {}", id);
			throw new ApiException(AppErrorCode.PROMOTION_NOT_FOUND);
		}
		else
		{
			var promo = promoOpt.get();
			
			validatePromo(request);
					
			var updatedPromo = buildPromo(request, promo.id());
			promoRepository.save(updatedPromo);
					
			log.info("Promotion was sucessfully updated. Id: {}", updatedPromo.id());
					
			return buildPromoResponse(updatedPromo);
					
		}
	}
	
	public void removePromo(String id) throws ApiException
	{
		var promoOpt = retrievePromoById(id);
		if (promoOpt.isPresent())
		{
			try
			{
				promoRepository.delete(id);
			}
			catch (ExecutionException | InterruptedException e)
			{
				log.error("Failed to delete a promotion from DB by id {}.", id, e);
				throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
			}
		}
		else
		{
			log.info("The provided promotion id was not found. id: {}", id);
		}
	}
	
	private void validatePromo(PromoRequest request) throws ApiException
	{
		var restaurantOpt = retrieveRestaurantById(request.restaurantId());
		if (restaurantOpt.isEmpty())
		{
			log.warn("Restaurant was not found. Id: {}", request.restaurantId());
			throw new ApiException(AppErrorCode.RESTAURANT_NOT_FOUND);
		}
		else
		{
			var restaurant = restaurantOpt.get();
			
			if (restaurant.products() != null && restaurant.products().size() > 0)
			{
				boolean productExistsInRestaurant = false;
				for (Product product : restaurant.products())
				{
					if (product.id().equals(request.productId()))
					{
						productExistsInRestaurant = true;
						break;
					}
				}
				
				if (!productExistsInRestaurant)
				{
					log.info("Product provided is not among the products offered by the restaurant. Product Id: {}", request.productId());
					throw new ApiException(AppErrorCode.INVALID_PRODUCT_RESTAURANT);
				}
			}
			else
			{
				log.info("The restaurant provided does not have any registered products. Restaurant Id: {}", restaurant.id());
				throw new ApiException(AppErrorCode.PRODUCT_NOT_FOUND);
			}
		}
	}
	
	private Promo buildPromo(PromoRequest request)
	{
		var id = UUID.randomUUID().toString();
		
		return buildPromo(request, id);
	}
	
	private Promo buildPromo(PromoRequest request, String id)
	{
		return new Promo(id, request.description(), request.restaurantId(), request.productId(), request.discountedPrice());
	}
	
	private PromoResponse buildPromoResponse(Promo promo) throws ApiException
	{
		//var products = restaurant.products().stream().map(this::buildProductResponse).toList();
		var restaurantOpt = retrieveRestaurantById(promo.restaurantId());
		var restaurant = restaurantOpt.get();
		
		String productName = null;				
		
		for (Product product : restaurant.products())
		{
			if (product.id().equals(promo.productId()))
			{
				productName = product.name();
				break;
			}
		}
		
		return new PromoResponse(promo.id(), promo.description(), restaurant.name(), productName, promo.discountedPrice());
	}

	private List<Promo> retrievePromos() throws ApiException
	{
		try
		{
			return promoRepository.getAll();
		}
		catch (ExecutionException | InterruptedException e)
		{
			log.error("Failed to read all promotions from DB.", e);
			throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
		}
	}
	
	private Optional<Promo> retrievePromoById(String id) throws ApiException
	{
		try
		{
			return promoRepository.getById(id);
		}
		catch (InterruptedException | ExecutionException e)
		{
			log.error("Failed to read a promotion from DB by id {}.", id, e);
			throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
		}
	}
	
	private Optional<Restaurant> retrieveRestaurantById(String id) throws ApiException
	{
		try
		{
			return restaurantRepository.getById(id);
		}
		catch (InterruptedException | ExecutionException e)
		{
			log.error("Failed to read an restaurant from DB by id {}.", id, e);
			throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
		}
	}
	
	private Optional<User> retrieveUserById(String id) throws ApiException
	{
		try
		{
			return userRepository.getById(id);
		}
		catch (InterruptedException | ExecutionException e)
		{
			log.error("Failed to read an user from DB by id {}.", id, e);
			throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
		}
	}
}
