package amai.org.conventions.model;


public class SpecialEventsProcessor {
	/**
	 * Modify properties of the event as needed for special event
	 *
	 * @param event
	 * @return whether further processing for the event description should stop
	 */
	public boolean processSpecialEvent(ConventionEvent event) {
		// By default, there are no special events
		return false;
	}
}
