package amai.org.conventions.model;

import java.io.Serializable;

public class Place implements Serializable {
	protected String name;

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
}
