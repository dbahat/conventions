package amai.org.conventions.networking;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandType;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;

public class SffStandsParser implements StandsParser {
	private static final String TAG = SffStandsParser.class.getCanonicalName();

	@Override
	public List<Stand> parse(InputStreamReader reader) {
		JsonElement root = JsonParser.parseReader(reader);
		// Fields:
		// String con
		// int count
		// String generated
		// Array booths
		// 		String id
		// 		String name
		// 		String category
		//		String Array tags
		// 		Object area
		//			String id
		//			String title
		// 		Object tableIds
		// 			int from
		//			int to
		//			int count
		//			String raw
		//			int Array list -> change to String Array?
		// 		String discountOrga -> change to boolean?
		// 		String url
		// 		String logo
		// 		String description
		//		String Array dates
		JsonObject rootObj = root.getAsJsonObject();
		JsonArray stands = rootObj.get("booths").getAsJsonArray();

		List<Stand> standsList = new LinkedList<>();

		int i = 0;
		for (JsonElement stand : stands) {
			try {
				JsonObject standObj = stand.getAsJsonObject();

				String name = standObj.get("name").getAsString();
				String description = hasNonNullProperty(standObj, "description") ? standObj.get("description").getAsString() : null;
				String category = hasNonNullProperty(standObj, "category") ? standObj.get("category").getAsString() : null;
				JsonArray jsonTags = hasNonNullProperty(standObj, "tags") ? standObj.get("tags").getAsJsonArray() : null;
				String area = null;
				if (standObj.has("area") && standObj.get("area").isJsonObject()) {
					JsonObject jsonArea = standObj.get("area").getAsJsonObject();
					area = hasNonNullProperty(jsonArea, "title") ? jsonArea.get("title").getAsString() : null;
				}
				JsonArray tableIdsList = null;
				if (standObj.has("tableIds") &&  standObj.get("tableIds").isJsonObject()) {
					JsonObject tableIds = standObj.get("tableIds").getAsJsonObject();
					if (hasNonNullProperty(tableIds, "list")) {
						tableIdsList = tableIds.get("list").getAsJsonArray();
					}
				}
				boolean discountOrga = hasNonNullProperty(standObj, "discountOrga") && standObj.get("discountOrga").getAsBoolean();
				String url = hasNonNullProperty(standObj, "url") ? standObj.get("url").getAsString() : null;
				JsonArray dates = hasNonNullProperty(standObj, "dates") ? standObj.get("dates").getAsJsonArray() : null;

				// Don't show stands with no area - they will not be displayed anywhere
				if (ParseUtils.isEmpty(area)) {
					Log.w(TAG, "Skipping stand with no stands area: " + name);
					continue;
				}

				Convention convention = Convention.getInstance();
				StandsArea standsArea = convention.findStandsAreaByName(area);
				if (standsArea == null) {
					Log.w(TAG, "Skipping stand with unknown stands area: " + name + ", area: " + area);
					continue;
				}

				StandType standType = convention.getOrAddStandType(category);

				List<String> locationIds = parseStringArray(tableIdsList);

				List<String> tags = parseStringArray(jsonTags);

				List<Dates.LocalDate> activeDays = parseLocalDates(dates);

				Stand currStand = new Stand()
					.withName(name)
					.withDescription(description)
					.withStandsArea(standsArea)
					.withWebsite(url)
					.withTypes(Collections.singletonList(standType))
					.withTags(tags)
					.withLocationIds(locationIds)
					.withDiscount(discountOrga)
					.withActiveDays(activeDays);

				standsList.add(currStand);
			} catch (Exception e) {
				Log.e(TAG, "Error parsing stand " + i + ", skipping", e);
			} finally {
				++i;
			}
		}

		return standsList;
	}

	private boolean hasNonNullProperty(JsonObject jsonObject, String key) {
		return jsonObject.has(key) && !jsonObject.get(key).isJsonNull();
	}

	private List<String> parseStringArray(JsonArray array) {
		if (array == null) {
			return null;
		}

		List<String> list = new ArrayList<>(array.size());
		for (JsonElement element : array) {
			if (element.isJsonPrimitive()) {
				list.add(element.getAsString());
			}
		}

		return list;
	}

	private List<Dates.LocalDate> parseLocalDates(JsonArray array) {
		List<String> rawDates = parseStringArray(array);
		if (rawDates == null) {
			return null;
		}

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Dates.getLocale());
		return CollectionUtils.map(rawDates, dateString -> {
			try {
				return new Dates.LocalDate(format.parse(dateString));
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
