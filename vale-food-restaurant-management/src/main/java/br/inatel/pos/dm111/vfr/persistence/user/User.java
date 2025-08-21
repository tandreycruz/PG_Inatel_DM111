package br.inatel.pos.dm111.vfr.persistence.user;

import java.util.List;

public record User(String id, String name, String email, String password, UserType type, List<String> favoriteProducts)
{
	public enum UserType {REGULAR, RESTAURANT}
}