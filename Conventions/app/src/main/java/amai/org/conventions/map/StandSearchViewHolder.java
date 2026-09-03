package amai.org.conventions.map;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Objects;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import sff.org.conventions.R;

public class StandSearchViewHolder extends RecyclerView.ViewHolder {
	private final TextView standName;
	private final TextView standFloor;

	public StandSearchViewHolder(View itemView) {
		super(itemView);
		standName = itemView.findViewById(R.id.stand_name);
		standFloor = itemView.findViewById(R.id.stand_floor);
	}

	public void setStand(Stand stand, Floor showFloorIfDifferent) {
		String name = stand.getName();
		String locationName = stand.getLocationName();
		Context context = itemView.getContext();
		standName.setText(name);
		standName.setTextColor(ThemeAttributes.getColor(context, R.attr.mapSearchText));

		Drawable image = ContextCompat.getDrawable(context, stand.getType().getImage());
		if (image != null) {
			int color = ThemeAttributes.getColor(context, R.attr.mapSearchImageColor);
			image.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
		}
		// this should be setCompoundDrawablesRelative(image, null, null, null) but in API 17 and 18 it appears on the wrong side.
		// After recycling it works. I tried calling setCompoundRelative twice (with same parameters and with nulls), calling setCompoundDrawables with nulls
		// and to change the order of method calls on standName but it didn't work.
		standName.setCompoundDrawables(null, null, image, null);

		String floorName = null;
		if (showFloorIfDifferent != null) {
			MapLocation location = Convention.getInstance().findStandsAreaLocation(stand.getStandsArea().getId());
			if (location != null && location.getFloor() != null && !Objects.equals(location.getFloor(), showFloorIfDifferent)) {
				floorName = location.getFloor().getName();
			}
		}
		if (floorName != null && !floorName.isEmpty()) {
			standFloor.setText(context.getString(R.string.stand_location_search, floorName, stand.getStandsArea().getName()));
		} else {
			standFloor.setText(stand.getStandsArea().getName());
		}
	}
}
