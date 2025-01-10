package amai.org.conventions.model;

import java.io.Serializable;

public class Place implements Serializable {
	protected String name;
	protected boolean isShelter;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Place withName(String name) {
		setName(name);
		return this;
	}

	public boolean isShelter() {
		return isShelter;
	}

	public void setShelter(boolean shelter) {
		isShelter = shelter;
	}

	public Place withShelter(boolean shelter) {
		setShelter(shelter);
		return this;
	}
}
