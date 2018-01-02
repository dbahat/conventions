package amai.org.conventions.networking;

import android.content.Context;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import amai.org.conventions.model.Update;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;

public class UpdatesRefresher {
	public interface OnUpdateFinishedListener {
		void onSuccess(int newUpdatesNumber);

		void onError(FacebookRequestError error);

		void onInvalidTokenError();
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
		if (!FacebookSdk.isInitialized()) {
			FacebookSdk.sdkInitialize(context.getApplicationContext());
		}
	}

	public void setIsRefreshInProgress(boolean isRefreshInProgress) {
		this.isRefreshInProgress = isRefreshInProgress;
	}

	public boolean isRefreshInProgress() {
		return isRefreshInProgress;
	}

	public void refreshFromServer(AccessToken accessToken, boolean enableNotificationAfterUpdate, final OnUpdateFinishedListener listener) {
		this.enableNotificationAfterUpdate = enableNotificationAfterUpdate;

		if (accessToken == null) {
			accessToken = AccessToken.getCurrentAccessToken();
			if (accessToken == null || accessToken.isExpired()) {
				// Try to fetch a new token if the previous one is invalid
				accessToken = AccessToken.getCurrentAccessToken();
				if (accessToken == null || accessToken.isExpired()) {
					isRefreshInProgress = false;
					listener.onInvalidTokenError();
					return;
				}
			}
		}

		isRefreshInProgress = true;

		GraphRequest request = GraphRequest.newGraphPathRequest(
				accessToken,
				Convention.getInstance().getFacebookFeedPath(),
				new GraphRequest.Callback() {
					@Override
					public void onCompleted(GraphResponse graphResponse) {
						if (graphResponse.getError() != null) {
							Log.i(TAG, "Updates refresh failed. Reason: " + graphResponse.getError().toString());
							isRefreshInProgress = false;
							listener.onError(graphResponse.getError());
						} else {
							List<Update> updatesFromResponse = parseAndFilterFacebookFeedResult(graphResponse);
							int newUpdatesNumber = 0;
							for (Update responseUpdate : updatesFromResponse) {
								Update currentUpdate = Convention.getInstance().getUpdate(responseUpdate.getId());
								if (currentUpdate == null) {
									++newUpdatesNumber;
								} else {
									responseUpdate.setIsNew(currentUpdate.isNew());
								}
							}
							// Update the model, so next time we can read them from cache.
							Convention.getInstance().addUpdates(updatesFromResponse, false);
							Convention.getInstance().getStorage().saveUpdates();
							isRefreshInProgress = false;
							listener.onSuccess(newUpdatesNumber);
						}
					}
				});

		Bundle parameters = new Bundle();
		Date newestUpdateTime = Convention.getInstance().getNewestUpdateTime();
		if (newestUpdateTime != null) {
			// Get all the new updates and the updates from 2 days before the last time in case an older post
			// was updated. The Facebook Graph API since parameter receives unix epoch time which is the number
			// of seconds instead of milliseconds.
			parameters.putLong("since", (newestUpdateTime.getTime() / 1000) - (2 * 24 * 60 * 60));
		}
		request.setParameters(parameters);
		request.executeAsync();
	}

	public boolean shouldEnableNotificationAfterUpdate() {
		return enableNotificationAfterUpdate;
	}

	private List<Update> parseAndFilterFacebookFeedResult(GraphResponse graphResponse) {
		List<Update> updates = new LinkedList<>();

		JSONObject response = graphResponse.getJSONObject();
		try {
			JSONArray data = response.getJSONArray("data");
			for (int i = 0; i < data.length(); i++) {
				JSONObject post = data.getJSONObject(i);
				if (post.has("id") && post.has("message") && post.has("created_time")) {

					String dateString = post.getString("created_time");
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Dates.getLocale());
					// Note: facebook dates are returns in UTC always
					simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
					Date date = simpleDateFormat.parse(dateString);

					String message = post.getString("message");

					// Add the link at the end if it doesn't exist in the message
					if (post.has("link")) {
						String link = post.getString("link");
						if (!message.contains(link)) {
							message += "\n\n" + link;
						}
					}

					Update update = new Update()
							.withId(post.getString("id"))
							.withIsNew(true)
							.withDate(date)
							.withText(message);

					updates.add(update);
				}
			}
		} catch (JSONException | ParseException e) {
			e.printStackTrace();
		}

		return updates;
	}
}
