package amai.org.conventions.map;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import sff.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.Stand;

public class StandViewHolder extends RecyclerView.ViewHolder {
	private final TextView standName;
	private final Button infoButton;
	private final boolean showLocation;

	public StandViewHolder(View itemView, boolean showLocation) {
		super(itemView);
		standName = itemView.findViewById(R.id.stand_name);
		infoButton = itemView.findViewById(R.id.open_stand_info);
		this.showLocation = showLocation;
	}

	public void setStand(Stand stand, boolean isSelected, boolean showInactiveIndication, boolean showDivider, OnClickListener onClickListener) {
		String name = stand.getName();
		String locationName = stand.getLocationName();
		Context context = itemView.getContext();
		// If the stand is inactive, show this instead of its location
		if (showInactiveIndication && !stand.isActive()) {
			name = context.getString(R.string.stand_name_inactive, name);
		} else  if (showLocation && locationName != null && !locationName.isEmpty()) {
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
			int color = ThemeAttributes.getColor(context, R.attr.standIconColor);
			image.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
		}
		// this should be setCompoundDrawablesRelative(image, null, null, null) but in API 17 and 18 it appears on the wrong side.
		// After recycling it works. I tried calling setCompoundRelative twice (with same parameters and with nulls), calling setCompoundDrawables with nulls
		// and to change the order of method calls on standName but it didn't work.
		standName.setCompoundDrawables(null, null, image, null);

		itemView.setOnClickListener(view -> onClickListener.onItemClicked(stand));
		if (onClickListener != null) {
			infoButton.setVisibility(View.VISIBLE);
			infoButton.setOnClickListener(view -> onClickListener.onItemInfoClicked(stand));
		} else {
			infoButton.setVisibility(View.GONE);
		}

		View divider = itemView.findViewById(R.id.stand_divider);
		if (showDivider) {
			divider.setVisibility(View.VISIBLE);
		} else {
			divider.setVisibility(View.GONE);
		}
	}

	public interface OnClickListener {
		void onItemClicked(Stand stand);
		void onItemInfoClicked(Stand stand);
	}
}
