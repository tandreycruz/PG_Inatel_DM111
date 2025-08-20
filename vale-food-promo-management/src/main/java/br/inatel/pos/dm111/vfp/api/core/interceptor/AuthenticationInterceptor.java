package br.inatel.pos.dm111.vfp.api.core.interceptor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.cloud.storage.HttpMethod;

import br.inatel.pos.dm111.vfp.api.core.ApiException;
import br.inatel.pos.dm111.vfp.api.core.AppErrorCode;
import br.inatel.pos.dm111.vfp.persistence.promo.Promo;
import br.inatel.pos.dm111.vfp.persistence.promo.PromoRepository;
import br.inatel.pos.dm111.vfp.persistence.restaurant.Restaurant;
import br.inatel.pos.dm111.vfp.persistence.restaurant.RestaurantRepository;
import br.inatel.pos.dm111.vfp.persistence.user.User;
import br.inatel.pos.dm111.vfp.persistence.user.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.impl.DefaultJws;
import io.jsonwebtoken.lang.Strings;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor
{
	private static final Logger log = LoggerFactory.getLogger(AuthenticationInterceptor.class);
	
	@Value("${vale-food.jwt.custom.issuer}")
	private String tokenIssuer;
	
	private final JwtParser jwtParser;
	private final UserRepository userRepository;
	private final RestaurantRepository restaurantRepository;
	private final PromoRepository promoRepository;
	
	public AuthenticationInterceptor(JwtParser jwtParser, UserRepository userRepository, RestaurantRepository restaurantRepository, PromoRepository promoRepository)
	{
		this.jwtParser = jwtParser;
		this.userRepository = userRepository;
		this.restaurantRepository = restaurantRepository;
		this.promoRepository = promoRepository;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		var method = request.getMethod();
		var uri = request.getRequestURI();
		
		//Jwt token validation
		var token = request.getHeader("Token");
		if (Strings.hasText(token))
		{
			token = request.getHeader("token");
		}
		
		if (!Strings.hasLength(token))
		{
			log.info("JWT token was not provided.");
			throw new ApiException(AppErrorCode.INVALID_USER_CREDENTIALS);
		}
		
		try
		{
			var jwt = (DefaultJws) jwtParser.parse(token);
			var payloadClaims = (Map<String, String>) jwt.getPayload();
			var issuer = payloadClaims.get("iss");
			var subject = payloadClaims.get("sub");
			var role = payloadClaims.get("role");
			
			var appJwtToken = new AppJwtToken(issuer, subject, role, method, uri);
			authenticateRequest(appJwtToken);
			
			return true;
		}
		catch (JwtException e)
		{
			log.error("Failure to validate the JWT token.", e);
			throw new ApiException(AppErrorCode.INVALID_USER_CREDENTIALS);
		}
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
	{
		log.debug("Request was processed sucessfully");
	}
	
	private void authenticateRequest(AppJwtToken appJwtToken) throws ApiException
	{
		if (!tokenIssuer.equals(appJwtToken.issuer()))
		{
			log.info("Provided token issuer is not valid.");
			throw new ApiException(AppErrorCode.INVALID_USER_CREDENTIALS);
		}
		
		var user = retrieveUserByEmail(appJwtToken.subject()).orElseThrow(() -> {
			log.info("User was not found for the provided token subject.");
			return new ApiException(AppErrorCode.INVALID_USER_CREDENTIALS);
		});
		
		if (!appJwtToken.role().equals(user.type().name()))
		{
			log.info("User type is invalid for the provided token role.");
			throw new ApiException(AppErrorCode.INVALID_USER_CREDENTIALS);
		}
		
		if (appJwtToken.uri().startsWith("/valefood/promos/"))
		{
			if (!appJwtToken.method().equals(HttpMethod.GET.name()))
			{
				if (!User.UserType.RESTAURANT.equals(user.type()))
				{
					log.info("User provided is not valid for this operation. UserId: {}", user.id());
					throw new ApiException(AppErrorCode.INVALID_USER_TYPE);
				}
			}
			
			if (appJwtToken.method().equals(HttpMethod.PUT.name()) || appJwtToken.method().equals(HttpMethod.DELETE.name()))
			{
				var splitUri = appJwtToken.uri().split("/");
				var pathPromoId = splitUri[3];
				var promo = retrievePromoById(pathPromoId).orElseThrow(() -> {
					log.info("Promotion does not exist");
					return new ApiException(AppErrorCode.INVALID_USER_CREDENTIALS);
				});
				
				var restaurantOfUser = retrieveRestaurantByUserId(user.id()).orElseThrow(() -> {
					log.info("Restaurant of user does not exist");
					return new ApiException(AppErrorCode.INVALID_USER_CREDENTIALS);
				});
				
				if (!restaurantOfUser.id().equals(promo.restaurantId()))
				{
					log.info("The restaurant of user didn't match to the restaurant linked to promotion. Restaurant of User Id: {}", restaurantOfUser.id());
					throw new ApiException(AppErrorCode.INVALID_USER_CREDENTIALS);
				}
			}
		}
	}

	private Optional<User> retrieveUserByEmail(String email) throws ApiException
	{
		try
		{
			return userRepository.getByEmail(email);
		}
		catch (InterruptedException | ExecutionException e)
		{
			log.error("Failed to read an users from DB by email.", e);
			throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
		}
	}
	/*
	private Optional<Restaurant> retrieveRestaurantById(String id) throws ApiException
	{
		try
		{
			return restaurantRepository.getById(id);
		}
		catch (InterruptedException | ExecutionException e)
		{
			log.error("Failed to read a restaurant from DB by id {}.", id, e);
			throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
		}
	}
	*/
	
	private Optional<Restaurant> retrieveRestaurantByUserId(String id) throws ApiException
	{
		try
		{
			return restaurantRepository.getByUserId(id);
		}
		catch (InterruptedException | ExecutionException e)
		{
			log.error("Failed to read a restaurant from DB by User Id {}.", id, e);
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
}
