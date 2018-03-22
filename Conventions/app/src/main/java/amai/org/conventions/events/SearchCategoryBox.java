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

import amai.org.conventions.ThemeAttributes;
import sff.org.conventions.R;

public class SearchCategoryBox extends LinearLayout {

	private SearchCategory searchCategory;
	private AppCompatCheckBox checkBox;
	private TextView textView;

	public SearchCategoryBox(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(this.getContext()).inflate(R.layout.search_category_box, this, true);
		checkBox = (AppCompatCheckBox) this.findViewById(R.id.search_category_checkbox);
		textView = (TextView) this.findViewById(R.id.search_category_text);
	}

	public void setSearchCategory(SearchCategory searchCategory) {
		textView.setText(searchCategory.getName());
		int color;
		if (searchCategory.hasColor()) {
			color = searchCategory.getColor();
		} else {
			color = ThemeAttributes.getColor(getContext(), R.attr.eventTimeDefaultBackgroundColor);
		}
		ColorStateList checkboxColors = new ColorStateList(
			new int[][]{
				new int[]{-android.R.attr.state_checked},
				new int[]{android.R.attr.state_checked}
			},
			new int[]{
				darkenColor(color, 2),
				color
		});
		CompoundButtonCompat.setButtonTintList(this.checkBox, checkboxColors);
		this.searchCategory = searchCategory;
	}

	public SearchCategory getSearchCategory() {
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

	private int darkenColor(int color, int factor) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		for (int i = 0; i < factor; ++i) {
			hsv[2] *= 0.9f;
		}
		color = Color.HSVToColor(hsv);
		return color;
	}
}
