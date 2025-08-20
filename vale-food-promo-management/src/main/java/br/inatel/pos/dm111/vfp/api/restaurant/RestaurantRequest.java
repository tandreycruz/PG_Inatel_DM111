package br.inatel.pos.dm111.vfp.api.restaurant;

import java.util.List;

public record RestaurantRequest(String id, String name, String address, String userId, List<String> categories, List<ProductRequest> products)
{
}
