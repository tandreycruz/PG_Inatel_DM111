package br.inatel.pos.dm111.vfr.publisher;

import java.util.List;

import br.inatel.pos.dm111.vfr.persistence.restaurant.Product;

public record RestaurantEvent(String id, String name, String address, String userId, List<String> categories, List<Product> products)
{	
}