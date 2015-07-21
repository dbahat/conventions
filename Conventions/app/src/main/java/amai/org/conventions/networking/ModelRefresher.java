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

    private static final String SERVER_ADDRESS = "http://2015.cami.org.il/wp-admin/admin-ajax.php?action=get_event_list";
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
            InputStreamReader reader = null;
            try {
                reader = new InputStreamReader((InputStream) request.getContent());
                List<ConventionEvent> eventList = new ModelParser().parse(reader);
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
}
