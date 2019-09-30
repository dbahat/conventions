package amai.org.conventions.model;

public class FloorLocation extends Place {
	private Floor floor;

	@Override
	public FloorLocation withName(String name) {
		super.withName(name);
		return this;
	}

	public Floor getFloor() {
		return floor;
	}

	public void setFloor(Floor floor) {
		this.floor = floor;
	}

	public FloorLocation withFloor(Floor floor) {
		setFloor(floor);
		return this;
	}
}
