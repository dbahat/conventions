package amai.org.conventions.model;

import java.io.Serializable;
import java.util.List;

import amai.org.conventions.utils.Objects;

public class StandsArea extends Place implements Serializable {
	int id;
	private List<Stand> stands;

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
