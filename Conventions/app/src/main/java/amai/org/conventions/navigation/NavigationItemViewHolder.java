package amai.org.conventions.navigation;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import sff.org.conventions.R;
import amai.org.conventions.ThemeAttributes;

public class NavigationItemViewHolder extends RecyclerView.ViewHolder {
	private final TextView textView;

	public NavigationItemViewHolder(View itemView) {
		super(itemView);
		textView = (TextView) itemView.findViewById(R.id.navigation_item_text);
	}

	public void setData(final NavigationItem item, final NavigationActivity currentActivity) {
		textView.setText(item.getText());
		Drawable icon = item.getIcon();

		if (currentActivity.getClass() == item.getActivity()) {
			int color = ThemeAttributes.getColor(currentActivity, R.attr.navigationPopupSelectedColor);
			textView.setTextColor(color);
			icon.mutate().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
			textView.setOnClickListener(null);
		} else {
			int color = ThemeAttributes.getColor(currentActivity, R.attr.navigationPopupNotSelectedColor);
			icon.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
			textView.setTextColor(color);
			textView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currentActivity.navigateToActivity(item.getActivity());
				}
			});
		}

		// TODO this should be setCompoundDrawablesRelative(icon, null, null, null) but in API 17 and 18 it appears on the wrong side.
		// I tried to fix it by setting the layout direction to RTL directly on the textview but it didn't work.
		textView.setCompoundDrawables(null, null, icon, null);
	}
}
