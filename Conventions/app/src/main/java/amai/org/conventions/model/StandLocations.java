package amai.org.conventions.model;

import java.util.HashMap;
import java.util.Map;

public class StandLocations {

	private Map<String, StandLocation> locationById = new HashMap<>();

	public StandLocations(StandLocation... locations) {
		for (StandLocation location : locations) {
			locationById.put(location.getId(), location);
		}
	}

	public StandLocation get(String id) {
		return locationById.get(id);
	}

	public boolean isConsecutive(StandLocation first, StandLocation second) {
		return first.getNext() != null && get(first.getNext()) == second;
	}
}
