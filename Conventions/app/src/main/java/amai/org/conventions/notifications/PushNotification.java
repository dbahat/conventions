package amai.org.conventions.notifications;


import java.io.Serializable;

public class PushNotification implements Serializable {
	public int id;
	public String messageId;
	public String message;
	public String category;

	public PushNotification(int id, String messageId, String message, String category) {
		this.id = id;
		this.messageId = messageId;
		this.message = message;
		this.category = category;
	}
}
