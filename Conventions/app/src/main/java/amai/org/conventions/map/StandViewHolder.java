package amai.org.conventions.map;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.Stand;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import sff.org.conventions.R;

public class StandViewHolder extends RecyclerView.ViewHolder {
	private final TextView standName;
	private final ImageView standTypeImage;
	private final Button infoButton;
	private final TextView standLocation;
	private final TextView standInactiveIndication;
	private final boolean showLocation;

	public StandViewHolder(View itemView, boolean showLocation) {
		super(itemView);
		standName = itemView.findViewById(R.id.stand_name);
		standTypeImage = itemView.findViewById(R.id.stand_type_image);
		infoButton = itemView.findViewById(R.id.open_stand_info);
		standLocation = itemView.findViewById(R.id.stand_location);
		standInactiveIndication = itemView.findViewById(R.id.stand_inactive_indication);
		this.showLocation = showLocation;
	}

	public void setStand(Stand stand, boolean isSelected, boolean showInactiveIndication, OnClickListener onClickListener) {
		String name = stand.getName();
		String locationName = stand.getLocationName();
		Context context = itemView.getContext();

		boolean bottomRowVisible = false;
		if (showInactiveIndication && !stand.isActive()) {
			standInactiveIndication.setVisibility(View.VISIBLE);
			bottomRowVisible = true;
		} else {
			standInactiveIndication.setVisibility(View.GONE);
		}

		if (showLocation && locationName != null && !locationName.isEmpty()) {
			standLocation.setVisibility(View.VISIBLE);
			standLocation.setText(locationName);
			bottomRowVisible = true;
		} else {
			standLocation.setVisibility(View.GONE);
		}

		standName.setText(name);
		standName.setTextColor(ThemeAttributes.getColor(context, R.attr.standNameColor));
		RelativeLayout.LayoutParams nameLayoutParams = (RelativeLayout.LayoutParams) standName.getLayoutParams();
		if (bottomRowVisible) {
			nameLayoutParams.removeRule(RelativeLayout.ALIGN_BASELINE);
		} else {
			nameLayoutParams.addRule(RelativeLayout.ALIGN_BASELINE, R.id.open_stand_info);
		}
		standName.setLayoutParams(nameLayoutParams);

		Drawable image = ContextCompat.getDrawable(context, stand.getType().getImage());
		if (image != null) {
			int color = ThemeAttributes.getColor(context, R.attr.standIconColor);
			image.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
		}
		standTypeImage.setImageDrawable(image);

		if (isSelected) {
			itemView.setBackground(ThemeAttributes.getDrawable(context, R.attr.selectedStandBackground));
		} else {
			itemView.setBackground(ThemeAttributes.getDrawable(context, R.attr.standBackground));
		}

		itemView.setOnClickListener(view -> onClickListener.onItemClicked(stand));
		if (onClickListener != null) {
			infoButton.setVisibility(View.VISIBLE);
			infoButton.setOnClickListener(view -> onClickListener.onItemInfoClicked(stand));
		} else {
			infoButton.setVisibility(View.GONE);
		}
	}

	public interface OnClickListener {
		void onItemClicked(Stand stand);
		void onItemInfoClicked(Stand stand);
	}
}
