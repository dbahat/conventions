package amai.org.conventions.model;

import java.util.Comparator;

public class ConventionEventComparator implements Comparator<ConventionEvent> {
	@Override
	public int compare(ConventionEvent lhs, ConventionEvent rhs) {
		// Order by start time, hall and end time
		int result = lhs.getStartTime().compareTo(rhs.getStartTime());
		if (result == 0) {
			result = lhs.getHall().getOrder() - rhs.getHall().getOrder();
		}
		if (result == 0) {
			result = lhs.getEndTime().compareTo(rhs.getEndTime());
		}
		return result;
	}
}
