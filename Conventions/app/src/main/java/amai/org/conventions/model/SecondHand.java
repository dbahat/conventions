package amai.org.conventions.model;

import android.support.annotation.NonNull;
import android.text.Html;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import amai.org.conventions.utils.Log;

class SecondHand {
	private static final String TAG = SecondHand.class.getCanonicalName();

	@NonNull
	protected SecondHandItem parseFormItem(JsonObject itemJson) {
		SecondHandItem item = new SecondHandItem();
		if (!itemJson.get("description").isJsonNull()) {
			item.setDescription(decodeHtml(itemJson.get("description").getAsString()));
		}
		JsonObject statusObject = itemJson.get("status").getAsJsonObject();
		item.setStatus(SecondHandItem.Status.getByServerStatus(statusObject.get("id").getAsInt()));
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
		item.setFormId(itemJson.get("formId").getAsString());
		return item;
	}

	private String decodeHtml(String string) {
		if (string == null) {
			return null;
		}
		// Using deprecated fromHtml() overload, since fromHtml(string, int) is only supported from api level 17
		// noinspection deprecation
		return Html.fromHtml(string).toString();
	}
}
