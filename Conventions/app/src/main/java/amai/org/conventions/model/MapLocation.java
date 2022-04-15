package amai.org.conventions.model;

import android.graphics.Color;

import java.util.Collections;
import java.util.List;

public class MapLocation {
	public static final int NO_TINT = 0;

	private int id;
	private Floor floor;
	private float x;
	private float y;
	private boolean doesMarkerPointUp = false;
	private List<? extends Place> places;
	private String name;
	private int markerResource;
	private int markerTintColorResource = NO_TINT;
	private boolean isMarkerResourceSVG;
	private int selectedMarkerResource;
	private int selectedMarkerTintColorResource = NO_TINT;
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

	public void setY(float y, boolean doesMarkerPointUp) {
		this.y = y;
		this.doesMarkerPointUp = doesMarkerPointUp;
	}

	public MapLocation withY(float y, boolean doesMarkerPointUp) {
		setY(y, doesMarkerPointUp);
		return this;
	}

	public MapLocation withY(float y) {
		return withY(y, false);
	}

	public boolean doesMarkerPointUp() {
		return doesMarkerPointUp;
	}

	public boolean hasSinglePlace() {
		return places != null && places.size() == 1;
	}

	public List<? extends Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<? extends Place> places) {
		this.places = places;
	}

	public MapLocation withPlaces(List<? extends Place> places) {
		setPlaces(places);
		return this;
	}

	public void setPlace(Place place) {
		this.places = Collections.singletonList(place);
	}

	public MapLocation withPlace(Place place) {
		setPlace(place);
		return this;
	}

	public String getName() {
		if (name != null) {
			return name;
		}
		if (places != null && places.size() > 0) {
			return places.get(0).getName();
		}
		return "";
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

	public int getMarkerTintColorResource() {
		return markerTintColorResource;
	}

	public void setMarkerResource(int markerResource, boolean isSVG, int tintColorResource) {
		this.markerResource = markerResource;
		this.isMarkerResourceSVG = isSVG;
		this.markerTintColorResource = tintColorResource;
	}

	public MapLocation withMarkerResource(int markerResource, boolean isMarkerResourceSVG) {
		return withMarkerResource(markerResource, isMarkerResourceSVG, Color.TRANSPARENT);
	}

	public MapLocation withMarkerResource(int markerResource, boolean isSVG, int tintColorResource) {
		setMarkerResource(markerResource, isSVG, tintColorResource);
		return this;
	}

	public boolean isMarkerResourceSVG() {
		return isMarkerResourceSVG;
	}

	public int getSelectedMarkerResource() {
		return selectedMarkerResource;
	}

	public int getSelectedMarkerTintColorResource() {
		return selectedMarkerTintColorResource;
	}

	public void setSelectedMarkerResource(int selectedMarkerResource, boolean isSVG, int tintColorResource) {
		this.selectedMarkerResource = selectedMarkerResource;
		this.isSelectedMarkerResourceSVG = isSVG;
		this.selectedMarkerTintColorResource = tintColorResource;
	}

	public MapLocation withSelectedMarkerResource(int selectedMarkerResource, boolean isSVG) {
		setSelectedMarkerResource(selectedMarkerResource, isSVG, Color.TRANSPARENT);
		return this;
	}

	public MapLocation withSelectedMarkerResource(int selectedMarkerResource, boolean isSVG, int tintColorResource) {
		setSelectedMarkerResource(selectedMarkerResource, isSVG, tintColorResource);
		return this;
	}

	public boolean isSelectedMarkerResourceSVG() {
		return isSelectedMarkerResourceSVG;
	}

	public boolean areAllPlacesHalls() {
		if (places == null || places.size() == 0) {
			return false;
		}
		for (Place place : places) {
			if (!(place instanceof Hall)) {
				return false;
			}
		}
		return true;
	}
}
