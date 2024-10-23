package amai.org.conventions.networking;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.model.Update;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.notifications.PushNotificationTopic;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.HttpConnectionCreator;
import amai.org.conventions.utils.Log;

public class UpdatesRefresher {
	private static final long MINIMUM_REFRESH_TIME = Dates.MILLISECONDS_IN_HOUR;

	public interface OnUpdateFinishedListener {
		void onSuccess(int newUpdatesNumber);
		void onError(Exception error);
	}

	private static UpdatesRefresher instance = null;

	private static final String TAG = UpdatesRefresher.class.getSimpleName();
	private boolean isRefreshInProgress = false;
	private boolean enableNotificationAfterUpdate;

	/**
	 * @param context any context
	 */
	public static synchronized UpdatesRefresher getInstance(Context context) {
		if (instance == null) {
			instance = new UpdatesRefresher(context);
		}
		return instance;
	}

	private UpdatesRefresher(Context context) {
	}

	public void setIsRefreshInProgress(boolean isRefreshInProgress) {
		this.isRefreshInProgress = isRefreshInProgress;
	}

	public boolean isRefreshInProgress() {
		return isRefreshInProgress;
	}

	public void refreshFromServer(boolean enableNotificationAfterUpdate, boolean force, final OnUpdateFinishedListener listener) {
		if (!force) {
			// Don't download if we recently updated the events
			Date lastUpdate = ConventionsApplication.settings.getLastUpdatesUpdateDate();
			if (lastUpdate != null && Dates.now().getTime() - lastUpdate.getTime() < MINIMUM_REFRESH_TIME) {
				isRefreshInProgress = false;
				listener.onSuccess(0);
				return;
			}
		}

		this.enableNotificationAfterUpdate = enableNotificationAfterUpdate;
		isRefreshInProgress = true;

		new AsyncTask<Void, Void, Integer>() {
			private Exception exception;

			@Override
			protected Integer doInBackground(Void... voids) {
				try {
					List<Update> updatesFromResponse = getUpdatesFromServer();

					List<Update> updatesToAdd = new ArrayList<>(updatesFromResponse.size());
					String testTopic = PushNotificationTopic.TOPIC_TEST.getTopic();
					boolean testTopicAllowed = ConventionsApplication.settings.getSharedPreferences().getBoolean(testTopic, false);
					int newUpdatesNumber = 0;
					for (Update responseUpdate : updatesFromResponse) {
						// Ignore test updates if not selected in the settings
						if ((!testTopicAllowed) && testTopic.equals(responseUpdate.getCategory())) {
							continue;
						}
						updatesToAdd.add(responseUpdate);

						Update currentUpdate = Convention.getInstance().getUpdate(responseUpdate.getId());
						if (currentUpdate == null) {
							++newUpdatesNumber;
						} else  {
							responseUpdate.setIsNew(currentUpdate.isNew());
						}
					}
					// Update the model, so next time we can read them from cache.
					Convention.getInstance().addUpdates(updatesToAdd, true);
					Convention.getInstance().getStorage().saveUpdates();
					ConventionsApplication.settings.setLastUpdatesUpdatedDate();
					isRefreshInProgress = false;
					return newUpdatesNumber;
				} catch (IOException e) {
					exception = e;
					Log.i(TAG, "Could not retrieve updates due to IOException: " + e.getMessage());
					isRefreshInProgress = false;
				} catch (Exception e) {
					exception = e;
					Log.e(TAG, "Could not retrieve updates: " + e.getMessage(), e);
					isRefreshInProgress = false;
				}
				return -1;
			}

            @Override
			protected void onPostExecute(Integer newUpdatesNumber) {
				if (exception == null) {
					listener.onSuccess(newUpdatesNumber);
				} else {
					listener.onError(exception);
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	protected List<Update> getUpdatesFromServer() throws Exception {
		URL updatesURL = getUpdatesURL();
		Log.i(TAG, "Fetching updates using URL: " + updatesURL);
		HttpURLConnection request = HttpConnectionCreator.createConnection(updatesURL);
		request.connect();
		try (InputStreamReader reader = new InputStreamReader((InputStream) request.getContent())) {
			return parseUpdates(reader);
		} finally {
			request.disconnect();
		}
	}

	private URL getUpdatesURL() {
		return Convention.getInstance().getUpdatesURL();
	}

	public boolean shouldEnableNotificationAfterUpdate() {
		return enableNotificationAfterUpdate;
	}

	private List<Update> parseUpdates(InputStreamReader reader) throws ParseException {
		List<Update> updates = new LinkedList<>();
		JsonElement root = JsonParser.parseReader(reader);
		JsonArray updatesArray = root.getAsJsonArray();
		for (int i = 0; i < updatesArray.size(); i++) {
			JsonObject updateObject = updatesArray.get(i).getAsJsonObject();
			if (updateObject.has("id") && updateObject.has("content") && updateObject.has("update_time")) {

				String dateString = updateObject.get("update_time").getAsString();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Dates.getLocale());
				Date date = simpleDateFormat.parse(dateString);

				String message = updateObject.get("content").getAsString();
				String category = null;
				if (updateObject.has("category") && updateObject.get("category").isJsonPrimitive()) {
					category = updateObject.get("category").getAsString();
				}

				Update update = new Update()
						.withId(updateObject.get("id").getAsString())
						.withIsNew(true)
						.withDate(Dates.conventionToLocalTime(date))
						.withText(message)
						.withCategory(category);

				updates.add(update);
			}
		}

		return CollectionUtils.filter(updates, item -> item.getText() != null);
	}
	private static class MockUpdatesRefresher extends UpdatesRefresher {
		private MockUpdatesRefresher(Context context) {
			super(context);
		}

		@Override
		protected List<Update> getUpdatesFromServer() throws Exception {
			return Arrays.asList(
				// To use this mock, change the initialization of the UpdatesRefresher
				// instance to MockUpdatesRefresher.
				// Add mock updates / extra logic here (you can keep a state since
				// this is a singleton).
			);
		}
	}
}
