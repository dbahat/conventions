package amai.org.conventions.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.utils.Objects;

public class MapLocationsAdapter extends BaseAdapter {
	private List<MapLocation> mapLocations;
	private Floor currentFloor;

	public MapLocationsAdapter(List<MapLocation> mapLocations) {
		this.mapLocations = mapLocations;
	}

	public void setFloor(Floor currentFloor) {
		this.currentFloor = currentFloor;
	}

	public void setMapLocations(List<MapLocation> mapLocations) {
		this.mapLocations = mapLocations;
	}

	public List<MapLocation> getMapLocations() {
		return mapLocations;
	}

	@Override
	public int getCount() {
		return mapLocations.size();
	}

	@Override
	public Object getItem(int position) {
		return mapLocations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final MapLocationViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_location_view_holder, parent, false);
			holder = new MapLocationViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (MapLocationViewHolder) convertView.getTag();
		}

		MapLocation location = mapLocations.get(position);
		holder.setLocation(location, !Objects.equals(location.getFloor(), currentFloor));
		return convertView;
	}
}
