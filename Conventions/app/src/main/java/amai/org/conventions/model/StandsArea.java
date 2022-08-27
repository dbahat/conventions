package amai.org.conventions.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import amai.org.conventions.utils.Objects;

public class StandsArea extends Place implements Serializable {
	private int id;
	private Integer imageResource;
	private float imageWidth;
	private float imageHeight;
	private List<Stand> stands = Collections.emptyList();
	private StandLocations standLocations = new StandLocations();

	public StandsArea() {
		id = ObjectIDs.getNextID();
	}

	public int getId() {
		return id;
	}

	@Override
	public StandsArea withName(String name) {
		super.withName(name);
		return this;
	}

	public boolean hasImageResource() {
		return imageResource != null;
	}

	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
	}

	public int getImageResource() {
		return imageResource;
	}

	public StandsArea withImageResource(int imageResource) {
		setImageResource(imageResource);
		return this;
	}

	public void setStands(List<Stand> stands) {
		this.stands = stands;

		for (Stand stand : stands) {
			stand.setStandsArea(this);
		}
	}

	public List<Stand> getStands() {
		return stands;
	}

	public StandsArea withStands(List<Stand> stands) {
		setStands(stands);
		return this;
	}

	public void setStandLocations(StandLocations standLocations) {
		this.standLocations = standLocations;
	}

	public StandLocations getStandLocations() {
		return standLocations;
	}

	public StandsArea withStandLocations(StandLocations standLocations) {
		setStandLocations(standLocations);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof StandsArea) {
			StandsArea other = (StandsArea) o;
			return Objects.equals(name, other.name) && Objects.equals(stands, other.stands);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, stands);
	}

	public float getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(float imageWidth) {
		this.imageWidth = imageWidth;
	}

	public StandsArea withImageWidth(float imageWidth) {
		setImageWidth(imageWidth);
		return this;
	}

	public float getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(float imageHeight) {
		this.imageHeight = imageHeight;
	}

	public StandsArea withImageHeight(float imageHeight) {
		setImageHeight(imageHeight);
		return this;
	}
}
