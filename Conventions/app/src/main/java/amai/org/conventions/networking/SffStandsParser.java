package amai.org.conventions.networking;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandType;
import amai.org.conventions.model.StandTypes;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Log;

public class SffStandsParser implements StandsParser {
	private static final String TAG = SffStandsParser.class.getCanonicalName();
	private final static int MAX_LOCATION_IDS = 30;

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
		// 		String category -> change to String array?
		// 		String area -> update values to only have the full name?
		// 		Object tableIds -> change to String array of location IDs?
		// 			int from
		//			int to
		//			int count
		//			String raw
		// 		String discountOrga -> change to boolean?
		// 		String url
		// 		String logo
		// 		-> Add description?
		//		-> Add when as String array?
		JsonObject rootObj = root.getAsJsonObject();
		JsonArray stands = rootObj.get("booths").getAsJsonArray();

		List<Stand> standsList = new LinkedList<>();

		int i = 0;
		for (JsonElement stand : stands) {
			try {
				JsonObject standObj = stand.getAsJsonObject();

				String name = standObj.get("name").getAsString();
				String category = standObj.get("category").getAsString();
				String area = standObj.get("area").getAsString();
				int tableIdsFrom = -1;
				int tableIdsTo = -1;
				if (standObj.has("tableIds") &&  standObj.get("tableIds").isJsonObject()) {
					JsonObject tableIds = standObj.get("tableIds").getAsJsonObject();
					if (tableIds.has("from") && !tableIds.get("from").isJsonNull()) {
						tableIdsFrom = tableIds.get("from").getAsInt();
					}
					if (tableIds.has("to") && !tableIds.get("to").isJsonNull()) {
						tableIdsTo = tableIds.get("to").getAsInt();
					}
				}
				boolean discountOrga = standObj.get("discountOrga").getAsBoolean();
				String url = standObj.get("url").getAsString();

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

				List<String> locationIds = parseLocationIds(name, tableIdsFrom, tableIdsTo);

				Stand currStand = new Stand()
					.withName(name)
					.withStandsArea(standsArea)
					.withWebsite(url)
					.withTypes(Collections.singletonList(standType))
					.withLocationIds(locationIds)
					.withDiscount(discountOrga);

				standsList.add(currStand);
			} catch (Exception e) {
				Log.e(TAG, "Error parsing stand " + i + ", skipping", e);
			} finally {
				++i;
			}
		}

		return standsList;
	}

	private List<String> parseLocationIds(String name, int from, int to) {
		if (from == -1 || to == -1) {
			Log.w(TAG, "stand " + name + ": missing from or to location IDs: from=" + from + ", to=" + to + ". Not setting location IDs.");
			return new ArrayList<>(); // Must be an array list to compare it to the existing stand
		} else if (to < from) {
			Log.e(TAG, "stand " + name + ": to < from in location IDs: from=" + from + ", to=" + to + ". Not setting location IDs.");
			return new ArrayList<>(); // Must be an array list to compare it to the existing stand
		}

		List<String> locationIds = new ArrayList<>(Math.min(to - from + 1, MAX_LOCATION_IDS));
		for (int curr = from; curr <= to; ++curr) {
			locationIds.add(String.valueOf(curr));
			if (locationIds.size() >= MAX_LOCATION_IDS) {
				Log.e(TAG, "stand " + name + ": too many location IDs, max is " + MAX_LOCATION_IDS + ": from=" + from + ", to=" + to + ". Skipping the rest.");
				break;
			}
		}
		return locationIds;
	}
}
