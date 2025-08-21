package br.inatel.pos.dm111.vfp.api.promo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.inatel.pos.dm111.vfp.api.core.ApiException;
import br.inatel.pos.dm111.vfp.api.core.AppError;
import br.inatel.pos.dm111.vfp.api.promo.PromoRequest;
import br.inatel.pos.dm111.vfp.api.promo.PromoResponse;
import br.inatel.pos.dm111.vfp.api.promo.service.PromoService;

@RestController
@RequestMapping("/valefood/promos")
public class PromoController
{
	private static final Logger log = LoggerFactory.getLogger(PromoController.class);
	
	private final PromoRequestValidator validator;
	private final PromoService service;
	
	
	
	public PromoController(PromoRequestValidator validator, PromoService service)
	{
		this.validator = validator;
		this.service = service;
	}

	@GetMapping
	public ResponseEntity<List<PromoResponse>> getAllPromos() throws ApiException
	{
		log.debug("Received request to list all promotions");
		
		var response = service.searchPromos();
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value = "/{promoId}")
	public ResponseEntity<PromoResponse> getPromoById(@PathVariable("promoId") String id) throws ApiException
	{
		log.debug("Received request to list an promotion by id: {}", id);
		
		var response = service.searchPromo(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value = "/user/{userId}")
	public ResponseEntity<List<PromoResponse>> getPromosFavoriteProductsByUser(@PathVariable("userId") String userId) throws ApiException
	{
		log.debug("Received request to list promotions by favorite products for user id: {}", userId);
		
		var response = service.searchPromosFavoriteProductsByUser(userId);
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PostMapping
	public ResponseEntity<PromoResponse> postPromo(@RequestBody PromoRequest request, BindingResult bindingResult) throws ApiException
	{
		log.debug("Received request to create a new promotion...");
		
		validateRequest(request, bindingResult);
		
		var response = service.createPromo(request);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PutMapping(value = "/{promoId}")
	public ResponseEntity<PromoResponse> putPromo(@RequestBody PromoRequest request, @PathVariable("promoId") String promoId, BindingResult bindingResult) throws ApiException
	{
		log.debug("Received request to update an promotion...");
		
		validateRequest(request, bindingResult);
		
		var response = service.updatePromo(request, promoId);
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@DeleteMapping(value = "/{promoId}")
	public ResponseEntity<PromoResponse> deletePromo(@PathVariable("promoId") String id) throws ApiException
	{
		log.debug("Received request to delete an promotion: id {}", id);
		
		service.removePromo(id);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	private void validateRequest(PromoRequest request, BindingResult bindingResult) throws ApiException
	{
		ValidationUtils.invokeValidator(validator, request, bindingResult);
		
		if (bindingResult.hasErrors())
		{
			var errors = bindingResult.getFieldErrors().stream().map(fe -> new AppError(fe.getCode(), fe.getDefaultMessage())).toList();
			throw new ApiException(HttpStatus.BAD_REQUEST, errors);
		}
	}
}
