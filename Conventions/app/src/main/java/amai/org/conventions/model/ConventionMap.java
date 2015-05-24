package amai.org.conventions.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ConventionMap {
	public static int FLOOR_NOT_FOUND = -1;

	private Floor lastLookedAtFloor = null;
	private List<Floor> floors = new ArrayList<>();
	private List<MapLocation> locations = new ArrayList<>();

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

	public List<MapLocation> findLocationsByHall(Hall hall) {
		List<MapLocation> locations = new LinkedList<>();
		for (MapLocation location : getLocations()) {
			if (location.getHall().equals(hall)) {
				locations.add(location);
			}
		}

		return locations;
	}

	public MapLocation findClosestLocation(List<MapLocation> locations) {
		if (locations.size() == 0) {
			return null;
		} else if (locations.size() == 1) {
			return locations.get(0);
		} else {
			final ConventionMap map = Convention.getInstance().getMap();
			Floor currMapFloor = map.getLastLookedAtFloor();
			if (currMapFloor != null) {
				final int currMapFloorIndex = map.floorNumberToFloorIndex(currMapFloor.getNumber());
				Collections.sort(locations, new Comparator<MapLocation>() {
					@Override
					public int compare(MapLocation lhs, MapLocation rhs) {
						// Return the closest location to the last looked at floor
						int distanceFromLeft = Math.abs(currMapFloorIndex - map.floorNumberToFloorIndex(lhs.getFloor().getNumber()));
						int distanceFromRight = Math.abs(currMapFloorIndex - map.floorNumberToFloorIndex(rhs.getFloor().getNumber()));
						return distanceFromLeft - distanceFromRight;
					}
				});
			}
			return locations.get(0);
		}
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
}
