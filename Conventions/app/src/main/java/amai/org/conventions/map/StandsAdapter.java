package amai.org.conventions.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Stand;

public class StandsAdapter extends BaseAdapter {
	private final boolean showLocations;
	private List<Stand> stands;
	private boolean colorImages;
	private String selectedStandName;
	private Floor currentFloor;

	public StandsAdapter(List<Stand> stands, boolean colorImages, boolean showLocations, String selectedStandName) {
		this.stands = stands;
		this.colorImages = colorImages;
		this.showLocations = showLocations;
		this.selectedStandName = selectedStandName;
	}

	public void setFloor(Floor currentFloor) {
		this.currentFloor = currentFloor;
	}

	public void setStands(List<Stand> stands) {
		this.stands = stands;
	}

	public List<Stand> getStands() {
		return stands;
	}

	@Override
	public int getCount() {
		return stands.size();
	}

	@Override
	public Object getItem(int position) {
		return stands.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final StandViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stand_view_holder, parent, false);
			holder = new StandViewHolder(convertView, colorImages, showLocations);
			convertView.setTag(holder);
		} else {
			holder = (StandViewHolder) convertView.getTag();
		}

		Stand stand = stands.get(position);
		holder.setStand(stand, selectedStandName != null && selectedStandName.equals(stand.getName()), currentFloor);
		return convertView;
	}
}
