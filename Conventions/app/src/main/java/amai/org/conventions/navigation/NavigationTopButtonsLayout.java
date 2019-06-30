package amai.org.conventions.navigation;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.customviews.AspectRatioImageView;

public class NavigationTopButtonsLayout extends LinearLayout {

    public NavigationTopButtonsLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setNavigationItems(final NavigationActivity currentActivity, List<NavigationItem> navigationItems) {
        removeAllViews();

        for (final NavigationItem item : navigationItems) {
            AspectRatioImageView imageView = new AspectRatioImageView(getContext());
            Drawable currentIcon = currentActivity.getClass() == item.getActivity() ? item.getSelectedItemIcon() : item.getIcon();
            imageView.setImageDrawable(currentIcon);
            int width = getResources().getDimensionPixelSize(R.dimen.navigation_settings_button_width);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
            int padding = getResources().getDimensionPixelSize(R.dimen.navigation_settings_button_padding);
            imageView.setPadding(padding, padding, padding, padding);
            addView(imageView);

            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentActivity.navigateToActivity(item.getActivity());
                }
            });
        }
    }
}
