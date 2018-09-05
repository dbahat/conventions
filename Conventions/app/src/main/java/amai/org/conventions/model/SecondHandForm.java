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
}
