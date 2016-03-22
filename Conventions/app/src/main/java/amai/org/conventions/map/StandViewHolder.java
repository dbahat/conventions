package amai.org.conventions.map;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.Stand;

public class StandViewHolder extends RecyclerView.ViewHolder {
	private final TextView standName;
	private final ImageView standImage;
	private boolean colorImage;

	public StandViewHolder(View itemView, boolean colorImage) {
		super(itemView);

		standName = (TextView) itemView.findViewById(R.id.stand_name);
		standImage = (ImageView) itemView.findViewById(R.id.stand_image);
		this.colorImage = colorImage;
	}

	public void setStand(Stand stand) {
		standName.setText(stand.getName());
		Resources resources = itemView.getContext().getResources();
		standImage.setImageDrawable(resources.getDrawable(stand.getType().getImage()));
		if (colorImage) {
			standImage.setColorFilter(ThemeAttributes.getColor(itemView.getContext(), R.attr.navigationPopupSelectedColor));
		}
	}
}
