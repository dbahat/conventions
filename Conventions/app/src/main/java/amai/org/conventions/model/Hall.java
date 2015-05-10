package amai.org.conventions.model;

import java.io.Serializable;

public class Hall implements Serializable {
    private String name;
    private int order;

    public Hall() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Hall withName(String name) {
        setName(name);
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
