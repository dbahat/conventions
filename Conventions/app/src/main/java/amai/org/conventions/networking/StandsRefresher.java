package amai.org.conventions.networking;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.HttpConnectionCreator;
import amai.org.conventions.utils.Log;
import sff.org.conventions.BuildConfig;

public class StandsRefresher {
	private static final String TAG = StandsRefresher.class.getCanonicalName();

	private static final long MINIMUM_REFRESH_TIME = Dates.MILLISECONDS_IN_HOUR;

	public interface OnRefreshFinishedListener {
		/** Called after refresh finished when successful */
		default void onSuccess() {}
		/** Called after refresh finished when an error occured */
		default void onError(Exception error) {}
	}

	private static StandsRefresher instance = null;
	private final ExecutorService mExecutor;
	private boolean isRefreshing = false;

	public static synchronized StandsRefresher getInstance() {
		if (instance == null) {
			instance = new StandsRefresher();
		}
		return instance;
	}

	private StandsRefresher() {
		mExecutor = Executors.newSingleThreadExecutor();
	}

	public boolean isRefreshing() {
		return isRefreshing;
	}

	/**
	 * Downloads the stands model from the server and updates it.
	 *
	 */
	public void refreshFromServer(boolean force, OnRefreshFinishedListener listener) {
		URL standsURL = Convention.getInstance().getStandsURL();

		// No stands / static stands
		if (standsURL == null) {
			listener.onSuccess();
			return;
		}

		if (!force) {
			// Don't download if the convention is over (there won't be any more updates to the stands...)
			if (Convention.getInstance().hasEnded()) {
				listener.onSuccess();
				return;
			}
			// Also don't download if we recently updated the stands
			Date lastUpdate = ConventionsApplication.settings.getLastStandsUpdateDate();
			if (lastUpdate != null && Dates.now().getTime() - lastUpdate.getTime() < MINIMUM_REFRESH_TIME) {
				listener.onSuccess();
				return;
			}
		}

		isRefreshing = true;

		mExecutor.submit(() -> {
			Exception ex = null;
			try {
				HttpURLConnection request = HttpConnectionCreator.createConnection(standsURL);
				request.connect();
				try (InputStreamReader reader = new InputStreamReader((InputStream) request.getContent())) {
					List<Stand> standsList = Convention.getInstance().getStandsParser().parse(reader);

					if (BuildConfig.DEBUG) {
						notifyIfStandsUpdated(Convention.getInstance().getStands(), standsList);
					}

					Convention.getInstance().setStands(standsList);
					ConventionsApplication.settings.setLastStandsUpdateDate();
				} finally {
					request.disconnect();
				}

				Convention.getInstance().getStorage().saveStands();
			} catch (IOException e) {
				Log.i(TAG, "Could not retrieve stands due to IOException: " + e.getMessage());
				ex = e;
			} catch (Exception e) {
				Log.e(TAG, "Could not retrieve stands: " + e.getMessage(), e);
				ex = e;
			} finally {
				isRefreshing = false;
			}

			Exception exception = ex;
			ConventionsApplication.runOnCurrentActivityUiThread(context -> {
				if (exception == null) {
					listener.onSuccess();
				} else {
					listener.onError(exception);
				}
			});
		});
	}

	private void notifyIfStandsUpdated(List<Stand> currentStands, List<Stand> newStands) {
		Log.i(TAG, "Stands refresh: Checking if stands are updated");
		if (currentStands == null) {
			currentStands = Collections.emptyList();
		}

		List<String> changes = new LinkedList<>();
		Map<String, Stand> currentStandsByName = new HashMap<>();
		for (Stand stand : currentStands) {
			currentStandsByName.put(stand.getName(), stand);
		}

		Map<String, Stand> newStandsByName = new HashMap<>();
		for (Stand stand : newStands) {
			newStandsByName.put(stand.getName(), stand);
		}

		// Check if there are new stands
		for (Stand stand : newStands) {
			if (!currentStandsByName.containsKey(stand.getName())) {
				changes.add("New stand: " + stand.getName());
			}
		}

		// Check if any stands were deleted
		for (Stand stand : currentStands) {
			if (!newStandsByName.containsKey(stand.getName())) {
				changes.add("Deleted stand: " + stand.getName());
			}
		}

		// Check for changed stands
		for (Stand newStand : newStands) {
			Stand currentStand = currentStandsByName.get(newStand.getName());
			if (currentStand == null) {
				continue;
			}
			if (!newStand.same(currentStand)) {
				changes.add("Changed stand: " + newStand.getName());
			}
		}

		for (String change : changes) {
			Log.i(TAG, "Stands refresh: " + change);
		}
		if (changes.isEmpty()) {
			Log.i(TAG, "Stands refresh: No changes");
		} else if (BuildConfig.DEBUG) { // Making double sure since the other actions in this method are not visible to the user
			// Show toast to know we should update the stands cache
			ConventionsApplication.runOnCurrentActivityUiThread(context -> Toast.makeText(context, changes.size() + " stand changes found", Toast.LENGTH_SHORT).show());
		}
	}
}
