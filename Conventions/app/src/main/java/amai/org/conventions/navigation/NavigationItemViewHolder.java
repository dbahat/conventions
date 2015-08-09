package amai.org.conventions.navigation;

import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;

public class NavigationItemViewHolder extends RecyclerView.ViewHolder {
	private final TextView textView;
	private final ImageView imageView;
	private final ViewGroup layout;

	public NavigationItemViewHolder(View itemView) {
		super(itemView);
		textView = (TextView) itemView.findViewById(R.id.navigation_item_text);
		imageView = (ImageView) itemView.findViewById(R.id.navigation_item_image);
		layout = (ViewGroup) itemView.findViewById(R.id.navigation_item_layout);
	}

	public void setData(final NavigationItem item, final NavigationActivity currentActivity) {
		textView.setText(item.getText());
		imageView.setImageDrawable(item.getIcon());


		if (currentActivity.getClass() == item.getActivity()) {
			int color = ThemeAttributes.getColor(currentActivity, R.attr.navigationPopupSelectedColor);
			textView.setTextColor(color);
			imageView.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
			layout.setOnClickListener(null);
		} else {
			int color = ThemeAttributes.getColor(currentActivity, R.attr.navigationPopupNotSelectedColor);
			imageView.setColorFilter(color);
			textView.setTextColor(color);
			layout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currentActivity.navigateToActivity(item.getActivity());
				}
			});
		}
	}
}
