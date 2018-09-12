package amai.org.conventions.model;

import java.io.Serializable;
import java.util.List;

public class SecondHandForm implements Serializable {
	private String id;
	private boolean isClosed;
	private List<SecondHandItem> items;
	private String status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SecondHandForm withId(String id) {
		setId(id);
		return this;
	}

	public void setClosed(boolean closed) {
		isClosed = closed;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setItems(List<SecondHandItem> items) {
		this.items = items;
	}

	public List<SecondHandItem> getItems() {
		return items;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean areAllItemsSold() {
		for (SecondHandItem item : getItems()) {
			if (item.getStatus() != SecondHandItem.Status.SOLD) {
				return false;
			}
		}
		return true;
	}

	public int getNumberOfSoldItems() {
		int number = 0;
		for (SecondHandItem item : getItems()) {
			if (item.getStatus() == SecondHandItem.Status.SOLD) {
				++number;
			}
		}
		return number;
	}

	public int getSoldItemsTotalPrice() {
		int total = 0;
		for (SecondHandItem item : getItems()) {
			if (item.getStatus() == SecondHandItem.Status.SOLD && item.getPrice() > -1) {
				total += item.getPrice();
			}
		}
		return total;
	}
}
