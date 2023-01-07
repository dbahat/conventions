package amai.org.conventions.networking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.model.Update;
import amai.org.conventions.model.conventions.Convention;
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

	@SuppressLint("StaticFieldLeak")
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
					URL updatesURL = getUpdatesURL();
					Log.i(TAG, "Fetching updates using URL: " + updatesURL);
					HttpURLConnection request = HttpConnectionCreator.createConnection(updatesURL);
					request.connect();
					try (InputStreamReader reader = new InputStreamReader((InputStream) request.getContent())) {
						List<Update> updatesFromResponse = parseUpdates(reader);
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
						Convention.getInstance().addUpdates(updatesFromResponse,
								// Since facebook has a unique ID per event, we can perform an incremental model update
								false);
						Convention.getInstance().getStorage().saveUpdates();
						ConventionsApplication.settings.setLastUpdatesUpdatedDate();
						isRefreshInProgress = false;
						return newUpdatesNumber;
					} finally {
						request.disconnect();
					}
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

            private URL getUpdatesURL() {
				Date newestUpdateTime = Convention.getInstance().getNewestUpdateTime();
				if (newestUpdateTime == null) {
					return Convention.getInstance().getUpdatesURL();
				}

				// Get all the new updates and the updates from 2 days before the last time in case an older post
				// was updated. The Facebook Graph API since parameter receives unix epoch time which is the number
				// of seconds instead of milliseconds.
				long timeToUpdateSince = (newestUpdateTime.getTime() / 1000) - (2 * 24 * 60 * 60);
                try {
                    return new URL(Uri.parse(Convention.getInstance().getUpdatesURL().toString())
                            .buildUpon()
                            .appendQueryParameter("since", String.valueOf(timeToUpdateSince))
                            .build()
                    .toString());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
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

	public boolean shouldEnableNotificationAfterUpdate() {
		return enableNotificationAfterUpdate;
	}

	private List<Update> parseUpdates(InputStreamReader reader) {
		List<UpdateDto> updateDtos = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
				.create()
				.fromJson(reader, UpdatesResponseDto.class).getData();

		return CollectionUtils
				.map(updateDtos, item -> new Update()
						.withId(item.getId())
						.withText(item.getMessage())
						.withDate(Dates.conventionToLocalTime(item.getCreatedTime()))
                        .withIsNew(true));
	}

	public static class UpdatesResponseDto {
		private List<UpdateDto> data;

		public List<UpdateDto> getData() {
			return data;
		}

		public UpdatesResponseDto setData(List<UpdateDto> data) {
			this.data = data;
			return this;
		}
	}

	public static class UpdateDto {
		private String id;
		private String message;
		@SerializedName("created_time")
		private Date createdTime;

		public String getId() {
			return id;
		}

		public UpdateDto setId(String id) {
			this.id = id;
			return this;
		}

		public String getMessage() {
			return message;
		}

		public UpdateDto setMessage(String message) {
			this.message = message;
			return this;
		}

		public Date getCreatedTime() {
			return createdTime;
		}

		public UpdateDto setCreatedTime(Date createdTime) {
			this.createdTime = createdTime;
			return this;
		}
	}
}