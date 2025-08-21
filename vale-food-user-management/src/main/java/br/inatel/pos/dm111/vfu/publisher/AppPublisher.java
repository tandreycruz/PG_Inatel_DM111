package br.inatel.pos.dm111.vfu.publisher;

import br.inatel.pos.dm111.vfu.persistence.user.User;

public interface AppPublisher
{
	default Event buildEvent(User user, Event.EventType eventType)
	{
		var userEvent = buildUserEvent(user);
		return new Event(eventType, userEvent);
	}
	
	default UserEvent buildUserEvent(User user)
	{
		return new UserEvent(user.id(), user.name(), user.email(), user.password(), user.type().name(), user.favoriteProducts());
	}
	
	boolean publishCreated(User user);
	
	boolean publishUpdated(User user);
	
	boolean publishDeleted(String id);
}