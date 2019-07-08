package amai.org.conventions.navigation;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;

public class NavigationItemViewHolder extends RecyclerView.ViewHolder {
	private final TextView textView;

	public NavigationItemViewHolder(View itemView) {
		super(itemView);
		textView = (TextView) itemView.findViewById(R.id.navigation_item_text);
	}

	public void setData(final NavigationItem item, final NavigationActivity currentActivity) {
		textView.setText(item.getText());
		boolean isCurrentItemSelected = currentActivity.getClass() == item.getActivity();
		configureDrawableIfNeeded(currentActivity, isCurrentItemSelected, item.getIcon());

		if (isCurrentItemSelected) {
			int color = ThemeAttributes.getColor(currentActivity, R.attr.navigationPopupNotSelectedColor);
			textView.setTextColor(color);

			textView.setOnClickListener(null);
			textView.setBackground(ContextCompat.getDrawable(textView.getContext(), R.drawable.cami2019_navigation_item_background_selected));
		} else {
			int color = ThemeAttributes.getColor(currentActivity, R.attr.navigationPopupNotSelectedColor);

			textView.setTextColor(color);
			textView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currentActivity.navigateToActivity(item.getActivity());
				}
			});
		}
	}

	private void configureDrawableIfNeeded(Context context, boolean isCurrentItemSelected, Drawable icon) {
		boolean shouldDisplayIcon = ThemeAttributes.getBoolean(context, R.attr.navigationItemsShouldDisplayIcon);
		if (!shouldDisplayIcon) {
			return;
		}

		if (isCurrentItemSelected) {
			int color = ThemeAttributes.getColor(context, R.attr.navigationPopupNotSelectedColor);
			icon.mutate().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
		} else {
			int color = ThemeAttributes.getColor(context, R.attr.navigationPopupNotSelectedColor);
			icon.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
		}
		// this should be setCompoundDrawablesRelative(icon, null, null, null) but in API 17 and 18 it appears on the wrong side.
		// I tried to fix it by setting the layout direction to RTL directly on the textview but it didn't work.
		textView.setCompoundDrawables(null, null, icon, null);
	}
}
