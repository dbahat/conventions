package amai.org.conventions.model;

import java.util.Comparator;

public class ConventionEventEndTimeComparator implements Comparator<ConventionEvent> {
	@Override
	public int compare(ConventionEvent lhs, ConventionEvent rhs) {
		// Order by end time, start time and hall
		int result = lhs.getEndTime().compareTo(rhs.getEndTime());
		if (result == 0) {
			result = lhs.getStartTime().compareTo(rhs.getStartTime());
		}
		if (result == 0) {
			result = lhs.getHall().getOrder() - rhs.getHall().getOrder();
		}
		return result;
	}
}
