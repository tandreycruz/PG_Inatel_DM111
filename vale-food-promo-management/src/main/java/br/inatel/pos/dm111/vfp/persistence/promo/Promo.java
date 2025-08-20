package br.inatel.pos.dm111.vfp.persistence.promo;

public record Promo(String id, String description, String restaurantId, String productId, float discountedPrice)
{
}