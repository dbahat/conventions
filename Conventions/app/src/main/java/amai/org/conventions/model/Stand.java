package amai.org.conventions.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Stand {
	private String name;
	private StandType type;
	private StandsArea standsArea;
	private List<String> locationIds;

	// Calculated from stands area and location IDs
	private List<StandLocation> locations;
	private String locationName;
	private String sort;
	private float imageX = -1;
	private float imageY = -1;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Stand withName(String name) {
		setName(name);
		return this;
	}

	public StandType getType() {
		return type;
	}

	public void setType(StandType type) {
		this.type = type;
	}

	public Stand withType(StandType type) {
		setType(type);
		return this;
	}

	public StandsArea getStandsArea() {
		return standsArea;
	}

	public void setStandsArea(StandsArea standsArea) {
		this.standsArea = standsArea;
		standLocationsUpdated();
	}

	public Stand withStandsArea(StandsArea standsArea) {
		setStandsArea(standsArea);
		return this;
	}

	public String getLocationName() {
		return locationName;
	}


	public String getSort() {
		return sort;
	}

	public void setLocationIds(List<String> locationIds) {
		this.locationIds = locationIds;
		standLocationsUpdated();
	}

	public Stand withLocationIds(String... locationIds) {
		setLocationIds(Arrays.asList(locationIds));
		return this;
	}

	public boolean hasImageCoordinates() {
		return getImageX() >= 0 && getImageY() >= 0;
	}

	public float getImageX() {
		return imageX;
	}

	public float getImageY() {
		return imageY;
	}

	private void standLocationsUpdated() {
		if (standsArea == null || locationIds == null) {
			return;
		}

		locations = calculateLocations();
		imageX = calculateImageX();
		imageY = calculateImageY();
		sort = calculateSort();
		locationName = calculateLocationName();
	}

	private List<StandLocation> calculateLocations() {
		List<StandLocation> locations = new LinkedList<>();
		for (String locationId : locationIds) {
			StandLocation location = getStandsArea().getStandLocations().get(locationId);
			if (location != null) {
				locations.add(location);
			}
		}
		Collections.sort(locations);
		return locations;
	}

	public List<StandLocation> getLocations() {
		return locations;
	}

	private float calculateImageX() {
		float minLeft = -1;
		float maxRight = 0;
		for (StandLocation location : getLocations()) {
			if (location.getLeft() < 0 || location.getRight() < 0) {
				continue;
			}
			minLeft = minLeft < 0 ? location.getLeft() : Math.min(minLeft, location.getLeft());
			maxRight = Math.max(maxRight, location.getRight());
		}
		return minLeft + ((maxRight - minLeft) / 2);
	}

	private float calculateImageY() {
		float minTop = -1;
		float maxBottom = 0;
		for (StandLocation location : getLocations()) {
			if (location.getTop() < 0 || location.getBottom() < 0) {
				continue;
			}
			minTop = minTop < 0 ? location.getTop() : Math.min(minTop, location.getTop());
			maxBottom = Math.max(maxBottom, location.getBottom());
		}
		return minTop + ((maxBottom - minTop) / 2);
	}

	private String calculateSort() {
		StringBuilder sortBuilder = new StringBuilder();
		for (StandLocation location : getLocations()) {
			sortBuilder.append(location.getSort()).append(",");
		}
		return sortBuilder.toString();
	}

	private String calculateLocationName() {
		List<StandLocation> locations = getLocations();
		if (locations.size() == 0) {
			return "";
		}
		StringBuilder nameBuilder = new StringBuilder();
		StandLocation first = null;
		StandLocation lastConsecutive = null;
		StandLocations standLocations = getStandsArea().getStandLocations();
		for (StandLocation location : locations) {
			if (first == null) {
				first = location;
				lastConsecutive = first;
				nameBuilder.append(first.getId());
				continue;
			}
			if (!standLocations.isConsecutive(lastConsecutive, location)) {
				if (lastConsecutive != first) {
					nameBuilder.append("-").append(lastConsecutive.getId());
				}
				first = location;
				lastConsecutive = first;
				nameBuilder.append(",").append(first.getId());
				continue;
			}
			lastConsecutive = location;
		}
		if (lastConsecutive != first) {
			nameBuilder.append("-").append(lastConsecutive.getId());
		}
		return nameBuilder.toString();
	}

	public interface StandType {
		int getTitle();
		int getImage();
		int ordinal();
		int compareTo(StandType t);
	}
}
