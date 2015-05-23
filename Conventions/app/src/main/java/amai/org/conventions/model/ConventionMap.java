package amai.org.conventions.model;

import java.util.ArrayList;
import java.util.List;

public class ConventionMap {
	private List<Floor> floors;
	private List<MapLocation> locations;

	public ConventionMap() {
		floors = new ArrayList<>();
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
}
