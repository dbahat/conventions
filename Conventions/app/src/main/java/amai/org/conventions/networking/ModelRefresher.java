package amai.org.conventions.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;

import amai.org.conventions.model.Convention;
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
