package amai.org.conventions.model;

import sff.org.conventions.R;

public class Stand {
	private String name;
	private StandType type;
	private StandsArea standsArea;
	private String locationName;
	private String sort;
	private float imageX;
	private float imageY;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Stand withName(String name) {
		setName(name);
		return this;
	}

	public StandType getType() {
		return type;
	}

	public void setType(StandType type) {
		this.type = type;
	}

	public Stand withType(StandType type) {
		setType(type);
		return this;
	}

	public StandsArea getStandsArea() {
		return standsArea;
	}

	public void setStandsArea(StandsArea standsArea) {
		this.standsArea = standsArea;
	}

	public Stand withStandsArea(StandsArea standsArea) {
		setStandsArea(standsArea);
		return this;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Stand withLocationName(String locationName) {
		setLocationName(locationName);
		return this;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public Stand withSort(String sort) {
		setSort(sort);
		return this;
	}

	public boolean hasImageCoordinates() {
		return getImageX() != 0 || getImageY() != 0; // Assuming there are no stands at (0, 0)
	}

	public float getImageX() {
		return imageX;
	}

	public void setImageX(float imageX) {
		this.imageX = imageX;
	}

	public Stand withImageX(float imageX) {
		setImageX(imageX);
		return this;
	}

	public float getImageY() {
		return imageY;
	}

	public void setImageY(float imageY) {
		this.imageY = imageY;
	}

	public Stand withImageY(float imageY) {
		setImageY(imageY);
		return this;
	}

	public interface StandType {
		int getTitle();
		int getImage();
		int ordinal();
		int compareTo(StandType t);
	}
}
