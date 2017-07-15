package amai.org.conventions.model;

import java.io.Serializable;

import amai.org.conventions.networking.AmaiModelConverter;

public class EventType implements Serializable {

	private int backgroundColor;
	private String description;

	public EventType(String description) {
		this(AmaiModelConverter.NO_COLOR, description);
	}

	public EventType(int backgroundColor, String description) {
		this.backgroundColor = backgroundColor;
		this.description = description;
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public boolean hasBackgroundColor() {
		return backgroundColor != AmaiModelConverter.NO_COLOR;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof EventType && equals((EventType) other);
	}

	private boolean equals(EventType other) {
		return other.getDescription().equals(getDescription());
	}

	@Override
	public int hashCode() {
		return getDescription().hashCode();
	}
}
