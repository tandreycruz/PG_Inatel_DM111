package br.inatel.pos.dm111.vfp.api.promo.controller;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import br.inatel.pos.dm111.vfp.api.promo.PromoRequest;

@Component
public class PromoRequestValidator implements Validator
{
	@Override
	public boolean supports(Class<?> clazz)
	{
		return PromoRequest.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors)
	{
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "description.empty", "Description is required!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "restaurantId", "restaurantId.empty", "Restaurant Id is required!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "productId", "productId.empty", "Product Id is required!");
		
		PromoRequest promoRequest = (PromoRequest) target;
		if (promoRequest.discountedPrice() <= 0.0f)
		{
	        errors.rejectValue("discountedPrice", "discountedPrice.invalid", "Discounted price must be greater than zero.");
	    }
	}
}
