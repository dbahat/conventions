package amai.org.conventions.model;

import java.io.Serializable;
import java.util.Date;

public class Update implements Serializable {
	private String id;
	private String text;
	private Date date;
	private boolean isNew;
	private String category;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Update withId(String id) {
		setId(id);
		return this;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setIsNew(boolean isNew) {
		this.isNew = isNew;
	}

	public Update withIsNew(boolean isNew) {
		setIsNew(isNew);
		return this;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Update withText(String text) {
		setText(text);
		return this;
	}

	public Update withDate(Date date) {
		setDate(date);
		return this;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Update withCategory(String category) {
		setCategory(category);
		return this;
	}
}
