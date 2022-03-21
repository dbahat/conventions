package amai.org.conventions.model;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import sff.org.conventions.R;

public class SecondHandSell extends SecondHand {
	private static final String TAG = SecondHandSell.class.getCanonicalName();
	private static final long MINIMUM_REFRESH_TIME = Dates.MILLISECONDS_IN_HOUR;

	private ConventionStorage storage;
	private List<SecondHandForm> forms;

	public SecondHandSell(ConventionStorage storage) {
		this.storage = storage;
		load();
	}

	public boolean shouldAutoRefresh() {
		// Don't try to refresh if there is nothing to update
		if (this.getForms().size() == 0) {
			return false;
		}
		// Only refresh if it hasn't been an hour since the previous refresh
		Date lastUpdate = ConventionsApplication.settings.getLastSecondHandUpdateDate();
		if (lastUpdate != null && Dates.now().getTime() - lastUpdate.getTime() < MINIMUM_REFRESH_TIME) {
			return false;
		}
		return true;
	}

	public boolean refresh(boolean force) {
		if (forms.size() == 0) {
			return true;
		}
		if (!force && !shouldAutoRefresh()) {
			return true;
		}
		Log.i(TAG, "Refreshing second hand");
		try {
			// Save user descriptions
			Map<String, String> userDescriptions = new HashMap<>();
			for (SecondHandForm form : forms) {
				for (SecondHandItem item : form.getItems()) {
					if (item.getUserDescription() != null) {
						userDescriptions.put(item.getId(), item.getUserDescription());
					}
				}
			}

			List<SecondHandForm> newForms;
			List<String> formIds = new ArrayList<>(forms.size());
			for (SecondHandForm form : forms) {
				formIds.add(form.getId());
			}
			URL refreshURL = Convention.getInstance().getSecondHandFormsURL(formIds);
			if (refreshURL != null) {
				HttpURLConnection request = HttpConnectionCreator.createConnection(refreshURL);
				request.connect();

				InputStreamReader reader = null;
				try {
					int responseCode = request.getResponseCode();
					if (responseCode != 200) {
						throw new RuntimeException("Could not read forms " + formIds + " , error code: " + responseCode);
					}
					reader = new InputStreamReader((InputStream) request.getContent());
					JsonElement root = JsonParser.parseReader(reader);
					JsonArray formsJson = root.getAsJsonArray();
					newForms = parseForms(formsJson);
				} finally {
					if (reader != null) {
						reader.close();
					}
					request.disconnect();
				}
			} else {
				// Temporary fix for refresh until we have refresh API
				newForms = new ArrayList<>(forms.size());
				for (SecondHandForm form : forms) {
					try {
						newForms.add(readForm(form.getId()));
					} catch (FormNotFoundException | NoItemsException e) {
						// Ignore deleted forms
						Log.e(TAG, "Second hand form " + form.getItems() + " not found during refresh: " + e.getClass().getSimpleName());
					}
				}
			}

			// Set items user descriptions
			for (SecondHandForm form : newForms) {
				for (SecondHandItem item : form.getItems()) {
					if (userDescriptions.get(item.getId()) != null) {
						item.setUserDescription(userDescriptions.get(item.getId()));
					}
				}
			}

			forms = newForms;
			save();
			ConventionsApplication.settings.setLastSecondHandUpdatedDate();
			Log.i(TAG, "Finished second hand refresh successfully");
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Could not refresh second hand forms list", e);
			return false;
		}
	}

	private SecondHandForm readForm(String id) throws Exception {
		id = normalizeFormId(id);
		HttpURLConnection request = HttpConnectionCreator.createConnection(Convention.getInstance().getSecondHandFormURL(id));
		request.connect();

		InputStreamReader reader = null;
		try {
			int responseCode = request.getResponseCode();
			// The API returns 500 in case the form number is invalid
			if (responseCode == 404 || responseCode == 500) {
				throw new FormNotFoundException();
			} else if (responseCode != 200) {
				throw new RuntimeException("Could not read form, error code: " + responseCode);
			}
			reader = new InputStreamReader((InputStream) request.getContent());
			JsonElement root = JsonParser.parseReader(reader);
			// The API can also return a json object instead of array when the form number is invalid
			if (!root.isJsonArray()) {
				throw new FormNotFoundException();
			}
			JsonArray formJson = root.getAsJsonArray();
			List<SecondHandForm> forms = parseForms(formJson);
			if (forms.size() == 0) {
				throw new FormNotFoundException();
			}
			// Ignoring the case that more than 1 form is returned from this API as it shouldn't happen
			return forms.get(0);
		} finally {
			if (reader != null) {
				reader.close();
			}
			request.disconnect();
		}
	}

	@NonNull
	private List<SecondHandForm> parseForms(JsonArray formsJson) {
		List<SecondHandForm> forms = new ArrayList<>(formsJson.size());
		for (int formIndex = 0; formIndex < formsJson.size(); ++formIndex) {
			JsonObject formJson = formsJson.get(formIndex).getAsJsonObject();
			SecondHandForm form = parseForm(formJson);
			forms.add(form);
		}
		return forms;
	}

	@NonNull
	private SecondHandForm parseForm(JsonObject formJson) {
		SecondHandForm form = new SecondHandForm();
		List<SecondHandItem> items = new LinkedList<>();
		JsonObject formStatusObject = formJson.get("status").getAsJsonObject();

		int formStatusId = formStatusObject.get("id").getAsInt();
		form.setClosed(formStatusId == 3);

		String formStatusText = formStatusObject.get("text").getAsString();
		form.setId(normalizeFormId(formJson.get("id").getAsString()));
		form.setStatus(formStatusText);

		// Items
		JsonArray itemsJson = formJson.getAsJsonArray("items");
		for (int itemIndex = 0; itemIndex < itemsJson.size(); ++itemIndex) {
			JsonObject itemJson = itemsJson.get(itemIndex).getAsJsonObject();
			SecondHandItem item = parseFormItem(itemJson);
			items.add(item);
		}
		if (items.size() == 0) {
			throw new NoItemsException();
		}
		Collections.sort(items, new Comparator<SecondHandItem>() {
			@Override
			public int compare(SecondHandItem item1, SecondHandItem item2) {
				return item1.getNumber() - item2.getNumber();
			}
		});
		form.setItems(items);
		return form;
	}

	public void save() {
		storage.saveSecondHandForms(forms);
	}

	private void load() {
		forms = storage.readSecondHandFromFile();
		if (forms == null) {
			forms = new LinkedList<>();
		}
	}

	public List<SecondHandForm> getForms() {
		return forms;
	}

	public void addForm(String id) {
		if (getForm(id) != null) {
			throw new FormAlreadyExists();
		}
		SecondHandForm form = null;
		try {
			form = readForm(id);
		} catch (RuntimeException e) {
			throw (RuntimeException) e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		forms.add(form);
		save();
	}

	public boolean deleteForm(String id) {
		SecondHandForm form = getForm(id);
		if (form != null) {
			forms.remove(form);
			save();
			return true;
		}
		return false;
	}

	@Nullable
	private SecondHandForm getForm(String id) {
		id = normalizeFormId(id);
		SecondHandForm form = null;
		for (SecondHandForm currentForm : forms) {
			if (currentForm.getId().equals(id)) {
				form = currentForm;
				break;
			}
		}
		return form;
	}

	private String normalizeFormId(String id) {
		if (id == null) {
			return null;
		}
		try {
			return Integer.valueOf(id).toString();
		} catch (NumberFormatException e) {
			return id;
		}
	}

	public String getSoldFormsMessage(Context context) {
		List<SecondHandForm> soldForms = new LinkedList<>();
		for (SecondHandForm form : getForms()) {
			if (!form.isClosed() && form.areAllItemsSold()) {
				soldForms.add(form);
			}
		}
		if (!soldForms.isEmpty()) {
			int totalPrice = 0;
			StringBuilder formIdsBuilder = new StringBuilder();
			boolean first = true;
			for (SecondHandForm form : soldForms) {
				if (first) {
					first = false;
				} else {
					formIdsBuilder.append(", ");
				}
				formIdsBuilder.append(form.getId());
				totalPrice += form.getSoldItemsTotalPrice();
			}

			String notificationMessage = context.getString(
				soldForms.size() > 1 ? R.string.second_hand_sold_forms_notification : R.string.second_hand_sold_form_notification,
				formIdsBuilder.toString(), totalPrice);
			return notificationMessage;
		}
		return null;
	}

	public static class NoItemsException extends RuntimeException {}
	public static class FormNotFoundException extends RuntimeException {}
	public static class FormAlreadyExists extends RuntimeException {}
}
