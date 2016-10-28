package com.evgenltd.mapper.ui.component.eventlog;

import com.evgenltd.mapper.ui.UIContext;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 18-06-2016 16:45
 */
public class Message implements Comparable<Message>	{
	private String title = "Message";
	private String text;
	private MessageType type;
	private Throwable exception;
	private LocalDateTime timestamp;

	private Message(@NotNull final MessageType type) {
		this.type = type;
		this.timestamp = LocalDateTime.now();
	}

	public static Message ofType(@NotNull final MessageType type)	{
		return new Message(type);
	}

	public static Message error()	{
		return ofType(MessageType.ERROR);
	}

	public static Message warning()	{
		return ofType(MessageType.WARNING);
	}

	public static Message information()	{
		return ofType(MessageType.INFORMATION);
	}

	public Message title(@NotNull final String title)	{
		this.title = title;
		return this;
	}

	public Message text(@NotNull final String text)	{
		this.text = text;
		return this;
	}

	public Message withException(@NotNull final Throwable exception)	{
		this.exception = exception;
		return this;
	}

	public void publish()	{
		UIContext.get().getEventLog().publish(this);
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public MessageType getType() {
		return type;
	}

	public Throwable getException() {
		return exception;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	@Override
	public int compareTo(@NotNull final Message o) {
		return this.getTimestamp().compareTo(o.getTimestamp());
	}
}
