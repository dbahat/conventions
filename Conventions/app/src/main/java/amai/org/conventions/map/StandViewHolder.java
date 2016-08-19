package amai.org.conventions.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.Stand;

public class StandViewHolder extends RecyclerView.ViewHolder {
	private final TextView standName;
	private boolean colorImage;

	public StandViewHolder(View itemView, boolean colorImage) {
		super(itemView);
		standName = (TextView) itemView.findViewById(R.id.stand_name);
		this.colorImage = colorImage;
	}

	public void setStand(Stand stand) {
		standName.setText(stand.getName());
		Context context = itemView.getContext();
		Drawable image = ContextCompat.getDrawable(context, stand.getType().getImage());
		if (image != null) {
			int color;
			if (colorImage) {
				color = ThemeAttributes.getColor(context, R.attr.standIconColor);
			} else {
				color = Color.BLACK;
			}
			image.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
		}
		// TODO this should be setCompoundDrawablesRelative(image, null, null, null) but in API 17 and 18 it appears on the wrong side.
		// After recycling it works. I tried calling setCompoundRelative twice (with same parameters and with nulls), calling setCompoundDrawables with nulls
		// and to change the order of method calls on standName but it didn't work.
		standName.setCompoundDrawables(null, null, image, null);
	}
}
