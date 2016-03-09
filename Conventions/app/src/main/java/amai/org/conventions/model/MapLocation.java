package amai.org.conventions.model;

public class MapLocation {
	private int id;
	private Floor floor;
	private float x;
	private float y;
	private Place place;
	private String name;
	private int markerResource;
	private int selectedMarkerResource;
	private float markerHeight = -1;

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

	public float getMarkerHeight() {
		return (markerHeight >= 0 ? markerHeight : getFloor().getDefaultMarkerHeight());
	}

	public void setMarkerHeight(float markerHeight) {
		this.markerHeight = markerHeight;
	}

	public MapLocation withMarkerHeight(float markerHeight) {
		setMarkerHeight(markerHeight);
		return this;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public MapLocation withX(float x) {
		setX(x);
		return this;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public MapLocation withY(float y) {
		setY(y);
		return this;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public MapLocation withPlace(Place place) {
		setPlace(place);
		return this;
	}

	public String getName() {
		if (name != null) {
			return name;
		}
		return getPlace().getName();
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
