package amai.org.conventions.model;

import java.io.Serializable;

import amai.org.conventions.utils.Objects;

public class StandType  implements Serializable {
	private int order;
	private String name;
	private int image;

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public StandType withOrder(int order) {
		setOrder(order);
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StandType withName(String name) {
		setName(name);
		return this;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}

	public StandType withImage(int image) {
		setImage(image);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof StandType) {
			StandType other = (StandType) o;
			return Objects.equals(name, other.name) && Objects.equals(order, other.order);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, order);
	}
}
