package amai.org.conventions.model;

import java.io.Serializable;

import amai.org.conventions.utils.Objects;

public class Hall extends Place implements Serializable {
	private int order;

	public Hall() {
	}

	@Override
	public Hall withName(String name) {
		super.withName(name);
		return this;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Hall withOrder(int order) {
		setOrder(order);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Hall) {
			Hall other = (Hall) o;
			return Objects.equals(name, other.name) && Objects.equals(order, other.order);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, order);
	}
}
