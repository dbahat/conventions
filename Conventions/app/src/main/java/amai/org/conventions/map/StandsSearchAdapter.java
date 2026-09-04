package amai.org.conventions.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.conventions.Convention;
import sff.org.conventions.R;

public class StandsSearchAdapter extends BaseAdapter {
	private List<Stand> stands;
	private Floor currentFloor;
	private boolean showInactiveIndication;

	public StandsSearchAdapter(List<Stand> stands) {
		this.stands = stands;
		// We only show the inactive indication during the convention
		this.showInactiveIndication = Convention.getInstance().hasStarted() && !Convention.getInstance().hasEnded();
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
		final StandSearchViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stand_search_view_holder, parent, false);
			holder = new StandSearchViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (StandSearchViewHolder) convertView.getTag();
		}

		Stand stand = stands.get(position);
		holder.setStand(stand, currentFloor, showInactiveIndication);
		return convertView;
	}
}
