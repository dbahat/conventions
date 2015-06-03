package amai.org.conventions.model;

public class ObjectIDs {
	private static int lastID = 1;

	public static int getNextID() {
		++lastID;
		return lastID;
	}
}
