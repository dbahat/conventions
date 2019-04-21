package amai.org.conventions.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.ConventionStorage;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.HttpConnectionCreator;
import amai.org.conventions.utils.Log;

public class SecondHandBuy extends SecondHand {
	private static final String TAG = SecondHandBuy.class.getCanonicalName();
	private static final long MINIMUM_REFRESH_TIME = Dates.MILLISECONDS_IN_HOUR;

	private ConventionStorage storage;
	private Map<String, SecondHandItem> favoriteItems;
	private List<SecondHandItem> items;

	public SecondHandBuy(ConventionStorage storage) {
		this.storage = storage;
		load();
	}

	public List<SecondHandItem> getItems() {
		return this.items;
	}

	private boolean shouldAutoRefresh() {
		// Only refresh if it hasn't been an hour since the previous refresh
		Date lastUpdate = ConventionsApplication.settings.getLastSecondHandSearchItemsUpdateDate();
		if (lastUpdate != null && Dates.now().getTime() - lastUpdate.getTime() < MINIMUM_REFRESH_TIME) {
			return false;
		}
		return true;
	}


	public boolean refresh(boolean force) {
		if (!force && !shouldAutoRefresh()) {
			return true;
		}
		Log.i(TAG, "Refreshing second hand buy");
		try {
			List<SecondHandItem> items = new LinkedList<>();
			URL refreshURL = Convention.getInstance().getSecondHandItemsURL(SecondHandItem.Status.READY);
			if (refreshURL != null) {
				HttpURLConnection request = HttpConnectionCreator.createConnection(refreshURL);
				request.connect();

				InputStreamReader reader = null;
				try {
					int responseCode = request.getResponseCode();
					if (responseCode != 200) {
						throw new RuntimeException("Could not read items, error code: " + responseCode);
					}
					reader = new InputStreamReader((InputStream) request.getContent());
					JsonParser jp = new JsonParser();
					JsonElement root = jp.parse(reader);
					JsonArray itemsJson = root.getAsJsonArray();
					for (int itemIndex = 0; itemIndex < itemsJson.size(); ++itemIndex) {
						JsonObject itemJson = itemsJson.get(itemIndex).getAsJsonObject();
						SecondHandItem item = parseFormItem(itemJson);
						items.add(item);
					}
				} finally {
					if (reader != null) {
						reader.close();
					}
					request.disconnect();
				}
			}

			Log.i(TAG, "Finished second hand buy refresh successfully");
			syncFavoriteItems(items);
			this.items = items;
			ConventionsApplication.settings.setLastSecondHandSearchItemsUpdateDate();
			saveItemsCache();
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Could not refresh second hand buy items", e);
			return false;
		}
	}

	private void syncFavoriteItems(List<SecondHandItem> items) {
		Map<String, SecondHandItem> existingItemIDs = new HashMap<>(items.size());
		for (SecondHandItem item : items) {
			existingItemIDs.put(item.getId(), item);
		}
		// Add non-existing favorite items to the items list with unknown status
		// Update the data of existing favorite items
		for (Map.Entry<String, SecondHandItem> pair : favoriteItems.entrySet()) {
			SecondHandItem favoriteItem = pair.getValue();
			if (!existingItemIDs.containsKey(favoriteItem.getId())) {
				favoriteItem.setStatus(SecondHandItem.Status.UNKNOWN);
				favoriteItem.setStatusText(null);
				items.add(favoriteItem);
			} else {
				favoriteItems.put(pair.getKey(), existingItemIDs.get(favoriteItem.getId()));
			}
		}
		saveFavorites();
	}

	public void addFavoriteItem(final SecondHandItem item) {
		if (!favoriteItems.containsKey(item.getId())) {
			favoriteItems.put(item.getId(), item);
			saveFavorites();
		}
	}

	public void removeFavoriteItem(final SecondHandItem item) {
		if (favoriteItems.containsKey(item.getId())) {
			favoriteItems.remove(item.getId());
			saveFavorites();
		}
	}

	public boolean isFavorite(SecondHandItem item) {
		return favoriteItems.containsKey(item.getId());
	}

	private void saveItemsCache() {
		storage.saveSecondHandSearchItems(items);
	}

	private void saveFavorites() {
		storage.saveSecondHandFavorites(new ArrayList<>(favoriteItems.values()));
	}

	private void load() {
		items = storage.readSecondHandSearchItemsFromFile();
		if (items == null) {
			items = new ArrayList<>();
		}
		favoriteItems = new HashMap<>();
		List<SecondHandItem> favoriteItemsFromStorage = storage.readSecondHandFavoritesFromFile();
		if (favoriteItemsFromStorage != null) {
			for (SecondHandItem item : favoriteItemsFromStorage) {
				favoriteItems.put(item.getId(), item);
			}
		}
	}

}
