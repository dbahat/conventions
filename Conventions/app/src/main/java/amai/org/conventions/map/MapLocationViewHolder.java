package amai.org.conventions.map;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import amai.org.conventions.R;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.MapLocation;

public class MapLocationViewHolder extends RecyclerView.ViewHolder {
	private final TextView locationName;
	private final TextView locationFloor;
	private final ImageView locationImage;

	public MapLocationViewHolder(View itemView) {
		super(itemView);

		locationName = (TextView) itemView.findViewById(R.id.map_location_name);
		locationFloor = (TextView) itemView.findViewById(R.id.map_location_floor);
		locationImage = (ImageView) itemView.findViewById(R.id.map_location_image);
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
		if (location.getPlace() instanceof Hall) {
			locationImage.setImageDrawable(resources.getDrawable(R.drawable.events_list));
		} else {
			locationImage.setImageDrawable(resources.getDrawable(R.drawable.ic_action_place));
		}
		locationImage.setColorFilter(resources.getColor(android.R.color.black));
	}
}
