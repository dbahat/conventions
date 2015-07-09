package amai.org.conventions.networking;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;

public class ModelRefresher {
    private static final String TAG = ModelRefresher.class.getCanonicalName();

    private static final String SERVER_ADDRESS = "http://2015.harucon.org.il/wp-admin/admin-ajax.php?action=get_event_list";
    private static final int CONNECT_TIMEOUT = 5000;

    /**
     * Downloads the model from the CAMI server.
     * @return true if the model retrieval completed successfully, false otherwise.
     */
    public boolean refreshFromServer() {
        try {
            URL url = new URL(SERVER_ADDRESS);

            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setConnectTimeout(CONNECT_TIMEOUT);
            request.connect();
            try {
                InputStreamReader reader = new InputStreamReader((InputStream) request.getContent());
                List<ConventionEvent> eventList = new ModelParser().parse(reader);
                Convention.getInstance().getStorage().saveEvents(eventList);

	            // Refresh the existing events - user input should not be taken from the server
	            List<ConventionEvent> previousEvents = Convention.getInstance().getEvents();
	            Map<String, ConventionEvent> previousEventsMap = new HashMap<>();
	            for (ConventionEvent event : previousEvents) {
		            previousEventsMap.put(event.getId(), event);
	            }

	            Convention.getInstance().setEvents(eventList);
	            for (ConventionEvent event : eventList) {
		            ConventionEvent previousEvent = previousEventsMap.get(event.getId());
		            // If previousEvent is null it means this event is new from the server
		            if (previousEvent != null) {
		                event.setUserInput(previousEvent.getUserInput());
		            }
	            }
            } finally {
                request.disconnect();
            }
        } catch (IOException e) {
	        Log.i(TAG, "Could not retrieve model due to IOException: " + e.getMessage());
	        return false;
        } catch (Exception e) {
            Log.e(TAG, "Could not retrieve model: " + e.getMessage(), e);
            return false;
        }

        return true;
    }
}
