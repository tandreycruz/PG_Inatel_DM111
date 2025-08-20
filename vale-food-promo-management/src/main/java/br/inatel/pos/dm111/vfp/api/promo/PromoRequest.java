package br.inatel.pos.dm111.vfp.api.promo;

public record PromoRequest(String description, String restaurantId, String productId, float discountedPrice)
{
}