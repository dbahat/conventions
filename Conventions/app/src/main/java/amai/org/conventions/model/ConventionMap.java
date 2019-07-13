package amai.org.conventions.model;

import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.utils.CollectionUtils;

public class ConventionMap {
	public static final int FLOOR_NOT_FOUND = -1;

	private Floor lastLookedAtFloor = null;
	private Floor defaultFloor = null;
	private List<Floor> floors = new ArrayList<>();
	private List<MapLocation> locations = new ArrayList<>();

	public boolean isAvailable() {
		return floors != null && floors.size() > 0;
	}

	public List<Floor> getFloors() {
		return floors;
	}

	public void setFloors(List<Floor> floors) {
		this.floors = floors;
	}

	public ConventionMap withFloors(List<Floor> floors) {
		setFloors(floors);
		return this;
	}

	public List<MapLocation> getLocations() {
		return locations;
	}

	public void setLocations(List<MapLocation> locations) {
		this.locations = locations;
	}

	public ConventionMap withLocations(List<MapLocation> locations) {
		setLocations(locations);
		return this;
	}

	public Floor getLastLookedAtFloor() {
		return lastLookedAtFloor;
	}

	public void setLastLookedAtFloor(Floor lastLookedAtFloor) {
		if (lastLookedAtFloor != null) {
			this.lastLookedAtFloor = lastLookedAtFloor;
		}
	}

	public Floor getDefaultFloor() {
		return defaultFloor;
	}

	public void setDefaultFloor(Floor defaultFloor) {
		this.defaultFloor = defaultFloor;
	}

	public ConventionMap withDefaultFloor(Floor defaultFloor) {
		setDefaultFloor(defaultFloor);
		return this;
	}

	public Floor findFloorByNumber(int number) {
		for (Floor floor : floors) {
			if (floor.getNumber() == number) {
				return floor;
			}
		}
		return null;
	}

	public Floor getBottomFloor() {
		return floors.get(0);
	}

	public Floor getTopFloor() {
		return floors.get(floors.size() - 1);
	}

	public List<MapLocation> findLocationsByName(final String name) {
		return CollectionUtils.filter(getLocations(), new CollectionUtils.Predicate<MapLocation>() {
			@Override
			public boolean where(MapLocation location) {
				List<? extends Place> places = location.getPlaces();
				if (places == null) {
					return false;
				}
				for (Place place : places) {
					if (place.getName().equals(name)) {
						return true;
					}
				}
				return false;
			}
		});
	}

	public List<MapLocation> findLocationsByStandsArea(final StandsArea standsArea) {
		return CollectionUtils.filter(getLocations(), new CollectionUtils.Predicate<MapLocation>() {
			@Override
			public boolean where(MapLocation location) {
				List<? extends Place> places = location.getPlaces();
				if (places == null) {
					return false;
				}
				for (Place place : places) {
					if (place instanceof StandsArea && ((StandsArea) place).getId() == standsArea.getId()) {
						return true;
					}
				}
				return false;
			}
		});
	}

	public int floorNumberToFloorIndex(int floorNumber) {
		boolean found = false;
		int index = 0;
		for (Floor curr : getFloors()) {
			if (curr.getNumber() == floorNumber) {
				found = true;
				break;
			}
			++index;
		}
		return found ? index : FLOOR_NOT_FOUND;
	}

	public List<MapLocation> findLocationsByFloor(final Floor floor) {
		return CollectionUtils.filter(getLocations(), new CollectionUtils.Predicate<MapLocation>() {
			@Override
			public boolean where(MapLocation location) {
				return location.getFloor().getNumber() == floor.getNumber();
			}
		});
	}

	public MapLocation findLocationById(final int id) {
		return CollectionUtils.findFirst(getLocations(), new CollectionUtils.Predicate<MapLocation>() {
			@Override
			public boolean where(MapLocation item) {
				return item.getId() == id;
			}
		});
	}
}
