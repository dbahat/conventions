package amai.org.conventions.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import amai.org.conventions.utils.Objects;

public class StandsArea extends Place implements Serializable {
	private int id;
	private Integer imageResource;
	private List<Stand> stands = Collections.emptyList();

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
}
