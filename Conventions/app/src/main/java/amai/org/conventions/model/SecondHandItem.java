package amai.org.conventions.model;

import java.io.Serializable;

import amai.org.conventions.utils.Log;

public class SecondHandItem implements Serializable {
	private static final String TAG = SecondHandItem.class.getCanonicalName();

	private String id;
	private String type;
	private String description;
	private String userDescription;
	private int price = -1; // Unknown
	private Status status;
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
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

	public enum Status {
		CREATED(1), // Not arrived at the stand yet
		READY(2), // In the stand
		SOLD(3),
		MISSING(4),
		RETURNED(5),
		DONATED(6),
		UNKNOWN(-1);

		private int serverStatus;
		Status(int serverStatus) {
			this.serverStatus = serverStatus;
		}

		public int getServerStatus() {
			return serverStatus;
		}

		static Status getByServerStatus(int serverStatus) {
			for (Status status : values()) {
				if (status.getServerStatus() == serverStatus) {
					return status;
				}
			}
			// This shouldn't happen, but if a new status appears without the application being updated it might
			Log.e(TAG, "Unknown status for second hand item: " + serverStatus);
			return UNKNOWN;
		}
	}
}
