package amai.org.conventions.utils;

public class Objects {
	public static <T> boolean equals(T o1, T o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		return o1.equals(o2);
	}

	public static int hash(Object... array) {
		// Copied from Arrays.hashCode to support lower API version
		if (array == null) {
			return 0;
		}
		int hashCode = 1;
		for (Object element : array) {
			int elementHashCode;

			if (element == null) {
				elementHashCode = 0;
			} else {
				elementHashCode = element.hashCode();
			}
			hashCode = 31 * hashCode + elementHashCode;
		}
		return hashCode;
	}

	public static <T extends Comparable> int compareTo(T o1, T o2, boolean nullIsFirst) {
		if (o1 == o2) {
			return 0;
		}
		if (o1 != null && o2 != null) {
			return o1.compareTo(o2);
		} else if (o1 == null) {
			return (nullIsFirst ? -1 : 1);
		} else { // o2 == null
			return (nullIsFirst ? 1 : -1);
		}
	}
}
