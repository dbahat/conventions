package amai.org.conventions.model;

import amai.org.conventions.utils.Objects;

public class Floor {
	private int number;
	private String name;
	private int imageResource;
	private float defaultMarkerHeight;
	private float imageHeight;
	private float imageWidth;
	private boolean isImageSVG;

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

	public boolean isImageSVG() {
		return isImageSVG;
	}

	public void setImageResource(int imageResource, boolean isImageSVG) {
		this.imageResource = imageResource;
		this.isImageSVG = isImageSVG;
	}

	public Floor withImageResource(int imageResource, boolean isImageSVG) {
		setImageResource(imageResource, isImageSVG);
		return this;
	}

	public float getDefaultMarkerHeight() {
		return defaultMarkerHeight;
	}

	public void setDefaultMarkerHeight(float defaultMarkerHeight) {
		this.defaultMarkerHeight = defaultMarkerHeight;
	}

	public Floor withDefaultMarkerHeight(float markerHeight) {
		setDefaultMarkerHeight(markerHeight);
		return this;
	}

	public float getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(float imageHeight) {
		this.imageHeight = imageHeight;
	}

	public Floor withImageHeight(float imageHeight) {
		setImageHeight(imageHeight);
		return this;
	}

	public float getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(float imageWidth) {
		this.imageWidth = imageWidth;
	}

	public Floor withImageWidth(float imageWidth) {
		setImageWidth(imageWidth);
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
