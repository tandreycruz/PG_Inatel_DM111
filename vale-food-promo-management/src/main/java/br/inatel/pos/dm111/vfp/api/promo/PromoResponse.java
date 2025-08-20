package br.inatel.pos.dm111.vfp.api.promo;

public record PromoResponse(String id, String description, String restaurantName, String productName, float discountedPrice)
{
}