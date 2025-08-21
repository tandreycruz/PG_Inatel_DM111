package br.inatel.pos.dm111.vfp.api.user;

import java.util.List;

public record UserRequest(String id, String name, String email, String password, String type, List<String> favoriteProducts)
{
}