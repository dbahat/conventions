package amai.org.conventions.model;

import amai.org.conventions.utils.Objects;

public class Floor {
	private int number;
	private String name;
	private int imageResource;
	private int markerHeight;

	public Floor(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Floor withName(String name) {
		setName(name);
		return this;
	}

	public int getImageResource() {
		return imageResource;
	}

	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
	}

	public Floor withImageResource(int imageResource) {
		setImageResource(imageResource);
		return this;
	}

	public int getMarkerHeight() {
		return markerHeight;
	}

	public void setMarkerHeight(int markerHeight) {
		this.markerHeight = markerHeight;
	}

	public Floor withMarkerHeight(int markerHeight) {
		setMarkerHeight(markerHeight);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Floor)) {
			return false;
		}

		Floor other = (Floor) o;

		return other.getNumber() == this.getNumber() && Objects.equals(other.getName(), this.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getNumber(), getName());
	}
}
