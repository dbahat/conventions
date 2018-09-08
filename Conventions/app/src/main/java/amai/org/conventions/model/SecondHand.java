package amai.org.conventions.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.ConventionStorage;
import amai.org.conventions.utils.HttpConnectionCreator;
import amai.org.conventions.utils.Log;

public class SecondHand {
	private static final String TAG = SecondHand.class.getCanonicalName();

	private ConventionStorage storage;
	private List<SecondHandForm> forms;

	public SecondHand(ConventionStorage storage) {
		this.storage = storage;
		load();
	}

	public boolean refresh() {
		if (forms.size() == 0) {
			return true;
		}
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

			List<String> formIds = new ArrayList<>(forms.size());
			for (SecondHandForm form : forms) {
				formIds.add(form.getId());
			}
			HttpURLConnection request = HttpConnectionCreator.createConnection(Convention.getInstance().getSecondHandFormsURL(formIds));
			request.connect();

			InputStreamReader reader = null;
			List<SecondHandForm> newForms;
			try {
				int responseCode = request.getResponseCode();
				if (responseCode != 200) {
					throw new RuntimeException("Could not read forms " + formIds + " , error code: " + responseCode);
				}
				reader = new InputStreamReader((InputStream) request.getContent());
				JsonParser jp = new JsonParser();
				JsonElement root = jp.parse(reader);
				JsonArray formsJson = root.getAsJsonArray();
				newForms = parseForms(formsJson);
				// Set items user descriptions
				for (SecondHandForm form : newForms) {
					for (SecondHandItem item : form.getItems()) {
						if (userDescriptions.get(item.getId()) != null) {
							item.setUserDescription(userDescriptions.get(item.getId()));
						}
					}
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
				request.disconnect();
			}
			forms = newForms;
			save();
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Could not refresh forms list", e);
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
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(reader);
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
			SecondHandItem item = parseFormItem(itemJson, form);
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

	@NonNull
	private SecondHandItem parseFormItem(JsonObject itemJson, SecondHandForm form) {
		SecondHandItem item = new SecondHandItem();
		if (!itemJson.get("description").isJsonNull()) {
			item.setDescription(itemJson.get("description").getAsString());
		}
		JsonObject statusObject = itemJson.get("status").getAsJsonObject();
		item.setStatus(convertItemStatus(statusObject.get("id").getAsInt()));
		item.setStatusText(statusObject.get("text").getAsString());
		item.setType(itemJson.get("category").getAsJsonObject().get("text").getAsString());
		JsonElement priceJson = itemJson.get("price");
		item.setPrice(-1);
		if (priceJson.isJsonPrimitive() && !priceJson.getAsString().isEmpty()) {
			try {
				item.setPrice(priceJson.getAsInt());
			} catch (NumberFormatException e) {
				Log.e(TAG, "Price is not a number in item " + item.getId() + ": " + priceJson.getAsString());
			}
		}
		item.setNumber(itemJson.get("indexInForm").getAsInt());
		item.setId(itemJson.get("id").getAsString());
		return item;
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

	private SecondHandItem.Status convertItemStatus(int statusId) {
		if (statusId == 2) { // Not arrived at the stand yet
			return SecondHandItem.Status.CREATED;
		} else if (statusId == 3) {
			return SecondHandItem.Status.SOLD;
		} else if (statusId == 4) {
			return SecondHandItem.Status.MISSING;
		} else { // in the stand but not sold (or withdrawn)
			return SecondHandItem.Status.READY;
		}
	}

	public static class NoItemsException extends RuntimeException {}
	public static class FormNotFoundException extends RuntimeException {}
	public static class FormAlreadyExists extends RuntimeException {}
}
