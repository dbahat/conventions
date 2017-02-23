package amai.org.conventions.events;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.EventType;

public class SearchCategoryBox extends LinearLayout {

	private EventType searchCategory;
	private AppCompatCheckBox checkBox;
	private TextView textView;

	public SearchCategoryBox(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(this.getContext()).inflate(R.layout.search_category_box, this, true);
		checkBox = (AppCompatCheckBox) this.findViewById(R.id.search_category_checkbox);
		textView = (TextView) this.findViewById(R.id.search_category_text);
	}

	public void setSearchCategory(EventType searchCategory) {
		textView.setText(searchCategory.getDescription());
		int color;
		if (searchCategory.hasBackgroundColor()) {
			color = darkenColor(searchCategory.getBackgroundColor());
		} else {
			color = ThemeAttributes.getColor(getContext(), R.attr.eventTimeDefaultBackgroundColor);
		}
		CompoundButtonCompat.setButtonTintList(this.checkBox, ColorStateList.valueOf(color));
		this.searchCategory = searchCategory;
	}

	public EventType getSearchCategory() {
		return searchCategory;
	}

	public void toggle() {
		checkBox.setChecked(!checkBox.isChecked());
	}

	public void check() {
		checkBox.setChecked(true);
	}

	public boolean isChecked() {
		return checkBox.isChecked();
	}

	private int darkenColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.9f;
		color = Color.HSVToColor(hsv);
		return color;
	}
}
