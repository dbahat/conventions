package amai.org.conventions.model;

import android.support.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.ConventionStorage;

public class SecondHand {
	private static final int CONNECT_TIMEOUT = 10000;

	private ConventionStorage storage;
	private List<SecondHandForm> forms;

	public SecondHand(ConventionStorage storage) {
		this.storage = storage;
		load();
	}

	public boolean refresh() {
		try {
			List<SecondHandForm> newForms = new LinkedList<>();
			for (SecondHandForm form : forms) {
				try {
					SecondHandForm newForm = readForm(form.getId());
					newForms.add(newForm);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			forms = newForms;
			save();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private SecondHandForm readForm(String id) throws IOException {
		id = normalizeFormId(id);
		HttpURLConnection request = (HttpURLConnection) Convention.getInstance().getSecondHandFormURL(id).openConnection();
		request.setConnectTimeout(CONNECT_TIMEOUT);
		request.connect();

		InputStreamReader reader = null;
		try {
			SecondHandForm form = new SecondHandForm().withId(id);
			List<SecondHandItem> items = new LinkedList<>();

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
			JsonArray itemsJson = root.getAsJsonArray();
			boolean first = true;
			for (int i = 0; i < itemsJson.size(); ++i) {
				JsonObject itemJson = itemsJson.get(i).getAsJsonObject();
				if (first) {
					first = false;
					String formStatus = itemJson.get("formStatus").getAsString();
					form.setClosed("closed".equals(formStatus));
				}
				SecondHandItem item = new SecondHandItem();
				item.setId(itemJson.get("formId").getAsString());
				item.setStatus(convertItemStatus(itemJson.get("itemStatus").getAsString()));
				item.setType(itemJson.get("itemCategory").getAsString());
				JsonElement priceJson = itemJson.get("price");
				item.setPrice(-1);
				if (priceJson.isJsonPrimitive() && !priceJson.getAsString().isEmpty()) {
					item.setPrice(priceJson.getAsInt());
				}
				item.setNumber(itemJson.get("formItemNumber").getAsInt());
				items.add(item);
			}
			if (items.size() == 0) {
				throw new NoItemsException();
			}
			form.setItems(items);
			return form;
		} finally {
			if (reader != null) {
				reader.close();
			}
			request.disconnect();
		}
	}

	private void save() {
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
		} catch (IOException e) {
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

	private SecondHandItem.Status convertItemStatus(String status) {
		if ("sold".equals(status)) {
			return SecondHandItem.Status.SOLD;
		} else if ("missing".equals(status)) {
			return SecondHandItem.Status.MISSING;
		} else { // unsold/withdrawn
			return SecondHandItem.Status.NOT_SOLD;
		}
	}

	public static class NoItemsException extends RuntimeException {}
	public static class FormNotFoundException extends RuntimeException {}
	public static class FormAlreadyExists extends RuntimeException {}
}
