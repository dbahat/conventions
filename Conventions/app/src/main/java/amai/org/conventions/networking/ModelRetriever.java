package amai.org.conventions.networking;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;

public class ModelRetriever {
    private static final String TAG = ModelRetriever.class.getSimpleName();

    private static final String SERVER_ADDRESS = "http://2015.harucon.org.il/wp-admin/admin-ajax.php?action=get_event_list";
    private static final int CONNECT_TIMEOUT = 5000;

    /**
     * Downloads the model from the CAMI server.
     * @return true if the model retrieval completed successfully, false otherwise.
     */
    public boolean retrieveFromServer() {
        try {
            URL url = new URL(SERVER_ADDRESS);

            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setConnectTimeout(CONNECT_TIMEOUT);
            request.connect();
            try {
                InputStreamReader reader = new InputStreamReader((InputStream) request.getContent());
                List<ConventionEvent> eventList = new ModelParser().parse(reader);
                Convention.getInstance().setEvents(eventList);
                Convention.getInstance().getStorage().saveEvents();
            } finally {
                request.disconnect();
            }
        } catch (IOException e) {
            Log.w(TAG, "failed to retrieve model with exception " + e.getMessage());
            return false;
        }

        return true;
    }
}
