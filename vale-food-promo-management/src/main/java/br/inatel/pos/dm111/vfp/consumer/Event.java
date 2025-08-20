package br.inatel.pos.dm111.vfp.consumer;

public record Event(EventType type, UserEvent event)
{
	public enum EventType {ADDED, UPDATED, DELETED};
}
