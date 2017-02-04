package amai.org.conventions.events;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.EventType;

/**
 * Layout which expands all the values defined in {@link EventType} as views of type {@link SearchCategoryBox}.
 */
public class SearchCategoriesLayout extends LinearLayout {

	private OnFilterSelectedListener onFilterSelectedListener;
	private int maxDisplayedCategories;
	private List<String> searchCategories;

	public SearchCategoriesLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSearchCategories(List<String> searchCategories) {
		removeAllViews();
		this.searchCategories = searchCategories;

		for (int i = 0; i < searchCategories.size(); i++) {
			// Don't add event type over the max configured display categories
			if (maxDisplayedCategories > 0 && i >= maxDisplayedCategories) {
				continue;
			}

			String searchCategory = searchCategories.get(i);
			SearchCategoryBox searchCategoryBox = createAndInitializeSearchCategoryBox(searchCategory);
			addView(searchCategoryBox);
		}

		// In case there are more categories then the maximum allowed, group all remaining categories under "other".
		if (maxDisplayedCategories > 0 && searchCategories.size() >= maxDisplayedCategories) {
			addView(createAndInitializeSearchCategoryBox(getContext().getString(R.string.other)));
		}
	}

	public void setMaxDisplayedCategories(int maxDisplayedCategories) {
		this.maxDisplayedCategories = maxDisplayedCategories;
	}

	private SearchCategoryBox createAndInitializeSearchCategoryBox(String searchCategory) {
		final SearchCategoryBox searchCategoryBox = new SearchCategoryBox(getContext(), null);
		searchCategoryBox.setSearchCategory(searchCategory);

		searchCategoryBox.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

		searchCategoryBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchCategoryBox.toggle();
				if (onFilterSelectedListener != null) {
					onFilterSelectedListener.onFilterSelected(getSelectedSearchCategories());
				}
			}
		});

		return searchCategoryBox;
	}

	private List<String> getSelectedSearchCategories() {

		List<String> selectedSearchCategories = new LinkedList<>();

		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child instanceof SearchCategoryBox) {
				SearchCategoryBox searchCategoryBox = (SearchCategoryBox) child;
				if (searchCategoryBox.isChecked()) {
					if (searchCategoryBox.getSearchCategory().equals(getContext().getString(R.string.other))) {
						for (int j = maxDisplayedCategories; j < searchCategories.size(); j++) {
							selectedSearchCategories.add(searchCategories.get(j));
						}
					} else {
						selectedSearchCategories.add(searchCategoryBox.getSearchCategory());
					}
				}
			}
		}

		return selectedSearchCategories;
	}

	public void setOnFilterSelectedListener(OnFilterSelectedListener onFilterSelectedListener) {
		this.onFilterSelectedListener = onFilterSelectedListener;
	}

	public void checkSearchCategory(String searchCategory) {
		// In case we got a category which is a part of the "other" category, check the "other" category instead
		if (searchCategories.contains(searchCategory) && searchCategories.indexOf(searchCategory) >= maxDisplayedCategories) {
			View child = getChildAt(getChildCount() - 1);
			if (child instanceof SearchCategoryBox) {
				SearchCategoryBox searchCategoryBox = (SearchCategoryBox) child;
				searchCategoryBox.check();
				return;
			}
		}

		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child instanceof SearchCategoryBox) {
				SearchCategoryBox searchCategoryBox = (SearchCategoryBox) child;
				if (searchCategoryBox.getSearchCategory().equals(searchCategory)) {
					searchCategoryBox.check();
					break;
				}
			}
		}
	}

	public interface OnFilterSelectedListener {
		void onFilterSelected(List<String> selectedSearchCategories);
	}
}
