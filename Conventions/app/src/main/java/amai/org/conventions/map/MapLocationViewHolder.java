package amai.org.conventions.map;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import amai.org.conventions.R;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.MapLocation;

public class MapLocationViewHolder extends RecyclerView.ViewHolder {
	private final TextView locationName;
	private final TextView locationFloor;

	public MapLocationViewHolder(View itemView) {
		super(itemView);

		locationName = (TextView) itemView.findViewById(R.id.map_location_name);
		locationFloor = (TextView) itemView.findViewById(R.id.map_location_floor);
	}

	public void setLocation(MapLocation location, boolean showFloor) {
		locationName.setText(location.getName());
		if (showFloor && location.getFloor() != null) {
			locationFloor.setVisibility(View.VISIBLE);
			locationFloor.setText(location.getFloor().getName());
		} else {
			locationFloor.setVisibility(View.GONE);
		}
		Resources resources = itemView.getContext().getResources();
		Drawable image;
		if (location.getPlace() instanceof Hall) {
			image = resources.getDrawable(R.drawable.events_list);
		} else {
			image = resources.getDrawable(R.drawable.ic_action_place);
		}
		if (image != null) {
			image.mutate().setColorFilter(resources.getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
		}
		// TODO this should be setCompoundDrawablesRelative(image, null, null, null) but in API 17 and 18 it appears on the wrong side.
		locationName.setCompoundDrawables(null, null, image, null);
	}
}
