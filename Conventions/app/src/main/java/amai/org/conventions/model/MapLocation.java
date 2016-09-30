package amai.org.conventions.model;

public class MapLocation {
	private int id;
	private Floor floor;
	private float x;
	private float y;
	private Place place;
	private String name;
	private int markerResource;
	private boolean isMarkerResourceSVG;
	private int selectedMarkerResource;
	private boolean isSelectedMarkerResourceSVG;
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

	public void setMarkerResource(int markerResource, boolean isMarkerResourceSVG) {
		this.markerResource = markerResource;
		this.isMarkerResourceSVG = isMarkerResourceSVG;
	}

	public MapLocation withMarkerResource(int markerResource, boolean isMarkerResourceSVG) {
		setMarkerResource(markerResource, isMarkerResourceSVG);
		return this;
	}

	public boolean isMarkerResourceSVG() {
		return isMarkerResourceSVG;
	}

	public int getSelectedMarkerResource() {
		return selectedMarkerResource;
	}

	public void setSelectedMarkerResource(int selectedMarkerResource, boolean isSelectedMarkerResourceSVG) {
		this.selectedMarkerResource = selectedMarkerResource;
		this.isSelectedMarkerResourceSVG = isSelectedMarkerResourceSVG;
	}

	public MapLocation withSelectedMarkerResource(int selectedMarkerResource, boolean isSelectedMarkerResourceSVG) {
		setSelectedMarkerResource(selectedMarkerResource, isSelectedMarkerResourceSVG);
		return this;
	}

	public boolean isSelectedMarkerResourceSVG() {
		return isSelectedMarkerResourceSVG;
	}
}
