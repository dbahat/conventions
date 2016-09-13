package amai.org.conventions.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import amai.org.conventions.BuildConfig;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.utils.Log;

public class ModelRefresher {
    private static final String TAG = ModelRefresher.class.getCanonicalName();

    private static final int CONNECT_TIMEOUT = 10000;

    /**
     * Downloads the model from the server.
     *
     * @return true if the model retrieval completed successfully, false otherwise.
     */
    public boolean refreshFromServer() {
	    // Don't download if the convention is over (there won't be any more updates to the events...)
	    if (Convention.getInstance().hasEnded()) {
		    return true;
	    }
        try {
            HttpURLConnection request = (HttpURLConnection) Convention.getInstance().getModelURL().openConnection();
            request.setConnectTimeout(CONNECT_TIMEOUT);
            request.connect();
            InputStreamReader reader = null;
            try {
                reader = new InputStreamReader((InputStream) request.getContent());
                List<ConventionEvent> eventList = Convention.getInstance().getModelParser().parse(reader);

	            if (BuildConfig.DEBUG) {
		            notifyIfEventsUpdated(Convention.getInstance().getEvents(), eventList);
	            }

                Convention.getInstance().setEvents(eventList);
            } finally {
                if (reader != null) {
                    reader.close();
                }
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
