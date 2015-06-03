package amai.org.conventions.model;

public class MapLocation {
	private int id;
	private Floor floor;
	private int x;
	private int y;
	private Hall hall;
	private String name;
	private int markerResource;
	private int selectedMarkerResource;

	public MapLocation() {
		id = ObjectIDs.getNextID();
	}

	public int getId() {
		return id;
	}

	public Floor getFloor() {
		return floor;
	}

	public void setFloor(Floor floor) {
		this.floor = floor;
	}

	public MapLocation withFloor(Floor floor) {
		setFloor(floor);
		return this;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public MapLocation withX(int x) {
		setX(x);
		return this;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public MapLocation withY(int y) {
		setY(y);
		return this;
	}

	public Hall getHall() {
		return hall;
	}

	public void setHall(Hall hall) {
		this.hall = hall;
	}

	public MapLocation withHall(Hall hall) {
		setHall(hall);
		return this;
	}

	public String getName() {
		if (name != null) {
			return name;
		}
		return getHall().getName();
	}

	public void setName(String name) {
		this.name = name;
	}

	public MapLocation withName(String name) {
		setName(name);
		return this;
	}

	public int getMarkerResource() {
		return markerResource;
	}

	public void setMarkerResource(int markerResource) {
		this.markerResource = markerResource;
	}

	public MapLocation withMarkerResource(int markerResource) {
		setMarkerResource(markerResource);
		return this;
	}

	public int getSelectedMarkerResource() {
		return selectedMarkerResource;
	}

	public void setSelectedMarkerResource(int selectedMarkerResource) {
		this.selectedMarkerResource = selectedMarkerResource;
	}

	public MapLocation withSelectedMarkerResource(int selectedMarkerResource) {
		setSelectedMarkerResource(selectedMarkerResource);
		return this;
	}
}
