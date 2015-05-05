package amai.org.conventions.model;

import java.util.Comparator;

import amai.org.conventions.model.ConventionEvent;

public class ConventionEventComparator implements Comparator<ConventionEvent> {
    @Override
    public int compare(ConventionEvent lhs, ConventionEvent rhs) {
        // Order by start time and hall
        int result = lhs.getStartTime().compareTo(rhs.getStartTime());
        if (result == 0) {
            result = lhs.getHall().getOrder() - rhs.getHall().getOrder();
        }
        return result;
    }
}
