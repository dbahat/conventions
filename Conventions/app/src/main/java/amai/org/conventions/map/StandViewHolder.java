package amai.org.conventions.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import amai.org.conventions.model.Floor;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Objects;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.Stand;

public class StandViewHolder extends RecyclerView.ViewHolder {
	private final TextView standName;
	private final TextView standFloor;
	private final boolean showLocation;
	private final boolean colorImage;

	public StandViewHolder(View itemView, boolean colorImage, boolean showLocation) {
		super(itemView);
		standName = (TextView) itemView.findViewById(R.id.stand_name);
		standFloor = itemView.findViewById(R.id.stand_floor);
		this.colorImage = colorImage;
		this.showLocation = showLocation;
	}

	public void setStand(Stand stand, boolean isSelected, Floor showFloorIfDifferent) {
		String name = stand.getName();
		String locationName = stand.getLocationName();
		Context context = itemView.getContext();
		if (showLocation && locationName != null && !locationName.isEmpty()) {
			name += " (" + locationName + ")";
		}
		standName.setText(name);
		if (isSelected) {
			standName.setTextColor(ThemeAttributes.getColor(context, R.attr.standsTypeTitleColor));
		} else {
			standName.setTextColor(ThemeAttributes.getColor(context, R.attr.mapSearchText));
		}
		Drawable image = ContextCompat.getDrawable(context, stand.getType().getImage());
		if (image != null) {
			int color;
			if (colorImage) {
				color = ThemeAttributes.getColor(context, R.attr.standIconColor);
			} else {
				color = ThemeAttributes.getColor(context, R.attr.mapSearchText);
			}
			image.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
		}
		// this should be setCompoundDrawablesRelative(image, null, null, null) but in API 17 and 18 it appears on the wrong side.
		// After recycling it works. I tried calling setCompoundRelative twice (with same parameters and with nulls), calling setCompoundDrawables with nulls
		// and to change the order of method calls on standName but it didn't work.
		standName.setCompoundDrawables(null, null, image, null);

		if (showFloorIfDifferent != null) {
			MapLocation location = Convention.getInstance().findStandsAreaLocation(stand.getStandsArea().getId());
			if (location != null && location.getFloor() != null && !Objects.equals(location.getFloor(), showFloorIfDifferent)) {
				standFloor.setVisibility(View.VISIBLE);
				standFloor.setText(location.getFloor().getName());
			} else {
				standFloor.setVisibility(View.GONE);
			}
		} else {
			standFloor.setVisibility(View.GONE);
		}
	}
}
