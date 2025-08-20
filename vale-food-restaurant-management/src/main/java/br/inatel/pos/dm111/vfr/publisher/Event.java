package br.inatel.pos.dm111.vfr.publisher;

public record Event(EventType type, RestaurantEvent event)
{
	public enum EventType {ADDED, UPDATED, DELETED};
}
