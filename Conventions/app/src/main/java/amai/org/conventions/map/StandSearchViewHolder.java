package amai.org.conventions.map;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Objects;
import amai.org.conventions.utils.Views;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import sff.org.conventions.R;

public class StandSearchViewHolder extends RecyclerView.ViewHolder {
	private final TextView standName;
	private final ImageView standTypeImage;
	private final Button infoButton;
	private final TextView standLocation;
	private final TextView standInactiveIndication;

	public StandSearchViewHolder(View itemView) {
		super(itemView);
		standName = itemView.findViewById(R.id.stand_name);
		standTypeImage = itemView.findViewById(R.id.stand_type_image);
		infoButton = itemView.findViewById(R.id.open_stand_info);
		standLocation = itemView.findViewById(R.id.stand_location);
		standInactiveIndication = itemView.findViewById(R.id.stand_inactive_indication);
	}

	public void setStand(Stand stand, boolean showInactiveIndication, List<String> keywordsToHighlight, StandSearchViewHolder.OnClickListener onClickListener) {
		String name = stand.getName();
		String locationName = stand.getStandsArea().getName();
		Context context = itemView.getContext();

		if (showInactiveIndication && !stand.isActive()) {
			standInactiveIndication.setVisibility(View.VISIBLE);
		} else {
			standInactiveIndication.setVisibility(View.GONE);
		}

		standLocation.setText(locationName);

		standName.setText(name);
		standName.setTextColor(ThemeAttributes.getColor(context, R.attr.standNameColor));
		RelativeLayout.LayoutParams nameLayoutParams = (RelativeLayout.LayoutParams) standName.getLayoutParams();
		standName.setLayoutParams(nameLayoutParams);

		Drawable image = ContextCompat.getDrawable(context, stand.getType().getImage());
		if (image != null) {
			int color = ThemeAttributes.getColor(context, R.attr.standIconColor);
			image.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
		}
		standTypeImage.setImageDrawable(image);

		itemView.setBackground(ThemeAttributes.getDrawable(context, R.attr.standBackground));

		if (onClickListener != null) {
			itemView.setOnClickListener(view -> onClickListener.onItemClicked(stand));
			infoButton.setVisibility(View.VISIBLE);
			infoButton.setOnClickListener(view -> onClickListener.onItemInfoClicked(stand, keywordsToHighlight));
		} else {
			itemView.setOnClickListener(null);
			infoButton.setVisibility(View.GONE);
		}

		// Highlight the keywords in the stand name
		if (keywordsToHighlight != null) {
			int highlightColor = ThemeAttributes.getColor(context, R.attr.standKeywordHighlightColor);
			for (String keyword : keywordsToHighlight) {
				if (!keyword.trim().isEmpty()) {
					Views.tryHighlightKeywordInTextView(standName, keyword, highlightColor);
				}
			}
		}
	}

	public interface OnClickListener {
		void onItemClicked(Stand stand);
		void onItemInfoClicked(Stand stand, List<String> keywordsToHighlight);
	}
}
