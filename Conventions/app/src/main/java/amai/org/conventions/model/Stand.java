package amai.org.conventions.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Objects;

public class Stand {
	private String name;
	private String description;
	private String website;
	private List<StandType> types;
	private StandsArea standsArea;
	private List<String> locationIds;
	private boolean discount;
	private List<Dates.LocalDate> activeDays;

	// Calculated from stands area and location IDs
	// They are transient so we don't serialize them. They are re-calculated when deserialized.
	private transient List<StandLocation> locations;
	private transient String locationName;
	private transient String sort;
	private transient float imageX = -1;
	private transient float imageY = -1;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Stand withDescription(String description) {
		setDescription(description);
		return this;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public Stand withWebsite(String website) {
		setWebsite(website);
		return this;
	}

	public StandType getType() {
		return types.get(0);
	}

	public List<StandType> getTypes() {
		return types;
	}

	public void setTypes(List<StandType> types) {
		this.types = types;
	}

	public Stand withTypes(List<StandType> types) {
		setTypes(types);
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

	public Stand withLocationIds(List<String> locationIds) {
		setLocationIds(locationIds);
		return this;
	}

	public boolean hasDiscount() {
		return discount;
	}

	public void setDiscount(boolean discount) {
		this.discount = discount;
	}

	public Stand withDiscount(boolean discount) {
		setDiscount(discount);
		return this;
	}

	public List<Dates.LocalDate> getActiveDays() {
		return activeDays;
	}

	public void setActiveDays(List<Dates.LocalDate> activeDays) {
		this.activeDays = activeDays;
	}

	public Stand withActiveDays(List<Dates.LocalDate> activeDays) {
		setActiveDays(activeDays);
		return this;
	}

	private boolean isActiveOn(Date date) {
		if (activeDays == null || activeDays.isEmpty()) {
			return true;
		}
		for (Dates.LocalDate activeDay : activeDays) {
			if (Dates.isSameDate(activeDay.getDate(), date)) {
				return true;
			}
		}
		return false;
	}

	public boolean isActive() {
		// Check if the stand is currently active, in the convention time
		Date nowInConventionTime = Dates.localToConventionTime(Dates.now());
		return isActiveOn(nowInConventionTime);
	}

	public boolean isAlwaysActive() {
		// Return true if active on all convention days (or active days are not set)
		if (activeDays == null || activeDays.isEmpty()) {
			return true;
		}
		Calendar[] eventDates = Convention.getInstance().getEventDates();
		for (Calendar eventDate : eventDates) {
			if (!isActiveOn(eventDate.getTime())) {
				return false;
			}
		}
		return true;
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

	/**
	 * Check if all non-calculated fields are equal
	 * @param other the stand to check against
	 * @return true if all their non-calculated fields are equal
	 */
	public boolean same(Stand other) {
		if (other == null) {
			return false;
		}
		return Objects.equals(this.name, other.name) &&
			Objects.equals(this.description, other.description) &&
			Objects.equals(this.website, other.website) &&
			Objects.equals(CollectionUtils.map(this.types, StandType::getName), CollectionUtils.map(other.types, StandType::getName)) &&
			// Checking the stands area name because we need to enable null, and we get the stand areas by name anyway
			Objects.equals(this.standsArea == null ? null : this.standsArea.getName(), other.standsArea == null ? null : other.standsArea.getName()) &&
			Objects.equals(this.locationIds, other.locationIds) &&
			Objects.equals(this.activeDays, other.activeDays);
	}
}
