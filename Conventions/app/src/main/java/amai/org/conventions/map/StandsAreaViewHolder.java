package amai.org.conventions.map;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.StandsArea;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import sff.org.conventions.R;

public class StandsAreaViewHolder extends RecyclerView.ViewHolder {
	private final TextView standsAraName;

	public StandsAreaViewHolder(View itemView) {
		super(itemView);
		standsAraName = itemView.findViewById(R.id.stands_area_name);
	}

	public void setStandsArea(StandsArea standsArea, OnClickListener onClickListener) {
		String name = standsArea.getName();
		Context context = itemView.getContext();

		standsAraName.setText(name);
		standsAraName.setTextColor(ThemeAttributes.getColor(context, R.attr.standsAreaNameColor));

		itemView.setOnClickListener(view -> onClickListener.onItemClicked(standsArea));
	}

	public interface OnClickListener {
		void onItemClicked(StandsArea standsArea);
	}
}
