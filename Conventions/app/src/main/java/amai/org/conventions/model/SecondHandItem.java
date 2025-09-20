package amai.org.conventions.model;

import java.io.Serializable;

public class SecondHandItem implements Serializable {
	private static final String TAG = SecondHandItem.class.getCanonicalName();

	// Not arrived at the stand yet
	public static final int ITEM_STATUS_CREATED = 1;
	// In the stand
	public static final int ITEM_STATUS_ACCEPTED = 2;
	public static final int ITEM_STATUS_CONFISCATED = 10;
	public static final int ITEM_STATUS_SOLD = 3;
	public static final int ITEM_STATUS_LOST = 4;
	// Statuses that happen when closing the form
	public static final int ITEM_STATUS_RETURNED = 5;
	public static final int ITEM_STATUS_DONATED = 6;
	public static final int ITEM_STATUS_ABANDONED = 8;
	// These statuses can happen if a lost item is found after the form was closed
	public static final int ITEM_STATUS_SOLD_AND_FOUND = 7;
	public static final int ITEM_STATUS_FOUND = 9;
	public static final int ITEM_STATUS_SUBMITTED_AND_FOUND = 11;
	public static final int ITEM_STATUS_DONATED_AND_FOUND = 12;
	// A buyer scanned the item in self service and hasn't paid yet
	public static final int ITEM_STATUS_RESERVED_FOR_PAYMENT = 13;
	// Item not found
	public static final int ITEM_STATUS_UNKNOWN = -1;

	private String id;
	private String type;
	private String description;
	private String userDescription;
	private int price = -1; // Unknown
	private int status;
	private String statusText;
	private int number;
	private String formId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getUserDescription() {
		return userDescription;
	}

	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}
}
