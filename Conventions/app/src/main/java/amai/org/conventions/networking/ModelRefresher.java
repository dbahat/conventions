package amai.org.conventions.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import amai.org.conventions.BuildConfig;
import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.HttpConnectionCreator;
import amai.org.conventions.utils.Log;

public class ModelRefresher {
	private static final String TAG = ModelRefresher.class.getCanonicalName();

	private static final long MINIMUM_REFRESH_TIME = Dates.MILLISECONDS_IN_HOUR;

	/**
	 * Downloads the model from the server.
	 *
	 * @return true if the model retrieval completed successfully, false otherwise.
	 */
	public boolean refreshFromServer(boolean force) {
		if (!force) {
			// Don't download if the convention is over (there won't be any more updates to the events...)
			if (Convention.getInstance().hasEnded()) {
				return true;
			}
			// Also don't download if we recently updated the events
			Date lastUpdate = ConventionsApplication.settings.getLastEventsUpdateDate();
			if (lastUpdate != null && Dates.now().getTime() - lastUpdate.getTime() < MINIMUM_REFRESH_TIME) {
				return true;
			}
		}

		try {
			HttpURLConnection request = HttpConnectionCreator.createConnection(Convention.getInstance().getModelURL());
			request.connect();
			try (InputStreamReader reader = new InputStreamReader((InputStream) request.getContent())) {
				List<ConventionEvent> eventList = Convention.getInstance().getModelParser().parse(reader);

				if (BuildConfig.DEBUG) {
					notifyIfEventsUpdated(Convention.getInstance().getEvents(), eventList);
				}

				rescheduleChangedEventNotifications(Convention.getInstance().getEvents(), eventList);

				Convention.getInstance().setEvents(eventList);
				ConventionsApplication.settings.setLastEventsUpdatedDate();
			} finally {
				request.disconnect();
			}
			Convention.getInstance().getStorage().saveEvents();
		} catch (IOException e) {
			Log.i(TAG, "Could not retrieve model due to IOException: " + e.getMessage());
			return false;
		} catch (Exception e) {
			Log.e(TAG, "Could not retrieve model: " + e.getMessage(), e);
			return false;
		}

		return true;
	}

	private void rescheduleChangedEventNotifications(List<ConventionEvent> currentEvents, List<ConventionEvent> newEvents) {
		Log.i(TAG, "Events refresh: Rescheduling alarms for events whose start or end time changed");
		Map<String, ConventionEvent> currentEventsById = new HashMap<>();
		for (ConventionEvent event : currentEvents) {
			currentEventsById.put(event.getId(), event);
		}

		for (ConventionEvent event : newEvents) {
			ConventionEvent currentEvent = currentEventsById.get(event.getId());
			if (currentEvent == null) {
				// If this is a new event, the current event would be null, and there are no alarms to schedule
				continue;
			}
			ConventionsApplication.getAppContext().rescheduleChangedEventAlarms(event, currentEvent);
		}
		Log.i(TAG, "Events refresh: finished updating alarms");
	}

	private void notifyIfEventsUpdated(List<ConventionEvent> currentEvents, List<ConventionEvent> newEvents) {
		Log.i(TAG, "Events refresh: Checking if events are updated");
		List<String> changes = new LinkedList<>();
		Map<String, ConventionEvent> currentEventsById = new HashMap<>();
		for (ConventionEvent event : currentEvents) {
			currentEventsById.put(event.getId(), event);
		}

		Map<String, ConventionEvent> newEventsById = new HashMap<>();
		for (ConventionEvent event : newEvents) {
			newEventsById.put(event.getId(), event);
		}

		// Check if there are new events
		for (ConventionEvent event : newEvents) {
			if (!currentEventsById.containsKey(event.getId())) {
				changes.add("New event: " + event.getTitle() + " (" + event.getId() + ")");
			}
		}

		// Check if any events were deleted
		for (ConventionEvent event : currentEvents) {
			if (!newEventsById.containsKey(event.getId())) {
				changes.add("Deleted event: " + event.getTitle() + " (" + event.getId() + ")");
			}
		}

		// Check for changed events
		for (ConventionEvent newEvent : newEvents) {
			ConventionEvent currentEvent = currentEventsById.get(newEvent.getId());
			if (currentEvent == null) {
				continue;
			}
			if (!newEvent.same(currentEvent)) {
				changes.add("Changed event: " + newEvent.getTitle() + " (" + newEvent.getId() + ")" +
						(newEvent.getTitle().equals(currentEvent.getTitle()) ? "" : ", previous name: " + currentEvent.getTitle()));
			}
		}

		for (String change : changes) {
			Log.i(TAG, "Events refresh: " + change);
		}
		if (changes.size() == 0) {
			Log.i(TAG, "Events refresh: No changes");
		}
	}
}
