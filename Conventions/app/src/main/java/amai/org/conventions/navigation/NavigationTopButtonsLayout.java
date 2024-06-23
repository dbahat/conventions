package amai.org.conventions.navigation;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import sff.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.customviews.AspectRatioImageView;
import androidx.annotation.Nullable;

public class NavigationTopButtonsLayout extends LinearLayout {

	public NavigationTopButtonsLayout(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public void setNavigationItems(final NavigationActivity currentActivity, List<NavigationItem> navigationItems) {
		removeAllViews();

		for (final NavigationItem item : navigationItems) {
			AspectRatioImageView imageView = new AspectRatioImageView(getContext());
			boolean isSelected = currentActivity.getClass() == item.getActivity();
			Drawable icon = item.getIcon();
			if (isSelected && item.getSelectedIcon() != null) {
				icon = item.getSelectedIcon();
			}
			int color;
			if (isSelected) {
				color = ThemeAttributes.getColor(currentActivity, R.attr.navigationPopupTopSelectedColor);
                int backgroundColor = ThemeAttributes.getColor(currentActivity, R.attr.navigationPopupSelectedBackground);
                imageView.setBackgroundColor(backgroundColor);
			} else {
				color = ThemeAttributes.getColor(currentActivity, R.attr.navigationPopupTopNotSelectedColor);
                imageView.setBackgroundColor(Color.TRANSPARENT);
			}
			icon.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
			imageView.setImageDrawable(icon);

			int width = getResources().getDimensionPixelSize(R.dimen.navigation_settings_button_width);
			imageView.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
			int padding = getResources().getDimensionPixelSize(R.dimen.navigation_settings_button_padding);
			imageView.setPadding(padding, padding, padding, padding);
			addView(imageView);

			if (isSelected) {
				imageView.setOnClickListener(null);
			} else {
				imageView.setOnClickListener(view -> currentActivity.navigateToActivity(item.getActivity()));
			}
		}
	}
}