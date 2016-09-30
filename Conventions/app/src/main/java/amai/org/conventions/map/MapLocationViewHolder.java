package amai.org.conventions.map;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import amai.org.conventions.model.MapLocation;
import sff.org.conventions.R;

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
		Context context = itemView.getContext();
		Drawable image;
		if (location.areAllPlacesHalls()) {
			image = ContextCompat.getDrawable(context, R.drawable.events_list);
		} else {
			image = ContextCompat.getDrawable(context, R.drawable.ic_action_place);
		}
		if (image != null) {
			image.mutate().setColorFilter(ContextCompat.getColor(context, android.R.color.black), PorterDuff.Mode.SRC_ATOP);
		}
		// TODO this should be setCompoundDrawablesRelative(image, null, null, null) but in API 17 and 18 it appears on the wrong side.
		locationName.setCompoundDrawables(null, null, image, null);
	}
}
