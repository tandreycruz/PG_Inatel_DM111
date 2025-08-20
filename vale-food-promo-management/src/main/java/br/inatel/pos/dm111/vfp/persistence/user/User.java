package br.inatel.pos.dm111.vfp.persistence.user;

public record User(String id, String name, String email, String password, UserType type)
{
	public enum UserType {REGULAR, RESTAURANT}
}
