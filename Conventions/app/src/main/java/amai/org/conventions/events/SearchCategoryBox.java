package amai.org.conventions.events;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import amai.org.conventions.R;

public class SearchCategoryBox extends LinearLayout {

	private String searchCategory;
	private CheckBox checkBox;
	private TextView textView;

	public SearchCategoryBox(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(this.getContext()).inflate(R.layout.search_category_box, this, true);
		checkBox = (CheckBox) this.findViewById(R.id.search_category_checkbox);
		textView = (TextView) this.findViewById(R.id.search_category_text);
	}

	public void setSearchCategory(String searchCategory) {
		textView.setText(searchCategory);
		this.searchCategory = searchCategory;
	}

	public String getSearchCategory() {
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
}
