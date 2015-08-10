package amai.org.conventions.networking;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

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

import amai.org.conventions.model.Convention;
import amai.org.conventions.model.Update;
import amai.org.conventions.utils.Dates;

public class UpdatesRefresher {
	public interface OnUpdateFinishedListener {
		void onSuccess(int newUpdatesNumber);
		void onError(FacebookRequestError error);
		void onInvalidTokenError();
	}

	private static UpdatesRefresher instance = null;

	private static final String TAG = UpdatesRefresher.class.getSimpleName();
	private static final String CAMI_EVENT_FEED_PATH = "/cami.org.il/posts";
	private boolean isRefreshInProgress = false;
	private boolean enableNotificationAfterUpdate;

	public static synchronized UpdatesRefresher getInstance(Context context) {
		if (instance == null) {
			instance = new UpdatesRefresher(context);
		}
		return instance;
	}

	private UpdatesRefresher(Context context) {
		FacebookSdk.sdkInitialize(context.getApplicationContext());
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
				CAMI_EVENT_FEED_PATH,
				new GraphRequest.Callback() {
					@Override
					public void onCompleted(GraphResponse graphResponse) {
						if (graphResponse.getError() != null) {
							Log.i(TAG, "Updates refresh failed. Reason: " + graphResponse.getError().toString());
							isRefreshInProgress = false;
							listener.onError(graphResponse.getError());
						} else {
							List<Update> updatesFromResponse = parseAndFilterFacebookFeedResult(graphResponse);
							int newUpdatesNumber = updatesFromResponse.size();
							// Update the model, so next time we can read them from cache.
							Convention.getInstance().addUpdates(updatesFromResponse);
							Convention.getInstance().getStorage().saveUpdates();
							isRefreshInProgress = false;
							listener.onSuccess(newUpdatesNumber);
						}
					}
				});

		Bundle parameters = new Bundle();
		Date newestUpdateTime = Convention.getInstance().getNewestUpdateTime();
		if (newestUpdateTime != null) {
			parameters.putLong("since", newestUpdateTime.getTime() / 1000);
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

					Update update = new Update()
							.withId(post.getString("id"))
							.withIsNew(true)
							.withDate(date)
							.withText(post.getString("message"));

					updates.add(update);
				}
			}
		} catch (JSONException | ParseException e) {
			e.printStackTrace();
		}

		return updates;
	}
}
