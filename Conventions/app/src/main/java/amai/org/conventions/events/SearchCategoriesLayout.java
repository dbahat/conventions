package amai.org.conventions.events;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.utils.CollectionUtils;

/**
 * Layout which expands all the values defined in {@link SearchCategory} as views of type {@link SearchCategoryBox}.
 */
public class SearchCategoriesLayout extends LinearLayout {

	private OnFilterSelectedListener onFilterSelectedListener;
	private int maxDisplayedCategories;
	private List<SearchCategory> searchCategories;

	public SearchCategoriesLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSearchCategories(List<SearchCategory> searchCategories) {
		removeAllViews();
		this.searchCategories = searchCategories;
		int actualCategoriesNumber = maxDisplayedCategories < searchCategories.size() ? maxDisplayedCategories - 1 : maxDisplayedCategories;

		for (int i = 0; i < searchCategories.size(); i++) {
			// Don't add event type over the max configured display categories
			if (maxDisplayedCategories > 0 && i >= actualCategoriesNumber) {
				continue;
			}

			SearchCategory searchCategory = searchCategories.get(i);
			SearchCategoryBox searchCategoryBox = createAndInitializeSearchCategoryBox(searchCategory);
			addView(searchCategoryBox);
		}

		// In case there are more categories then the maximum allowed, group all remaining categories under "other".
		if (maxDisplayedCategories > 0 && searchCategories.size() > maxDisplayedCategories) {
			addView(createAndInitializeSearchCategoryBox(new SearchCategory(
					getContext().getString(R.string.other),
					// Have the other category color the same as the first category to get aggregated into it
					searchCategories.get(maxDisplayedCategories - 1).getColor())));
		}
	}

	public void setMaxDisplayedCategories(int maxDisplayedCategories) {
		this.maxDisplayedCategories = maxDisplayedCategories;
	}

	private SearchCategoryBox createAndInitializeSearchCategoryBox(SearchCategory searchCategory) {
		final SearchCategoryBox searchCategoryBox = new SearchCategoryBox(getContext(), null);
		searchCategoryBox.setSearchCategory(searchCategory);

		searchCategoryBox.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

		searchCategoryBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchCategoryBox.toggle();
				if (onFilterSelectedListener != null) {
					onFilterSelectedListener.onFilterSelected(getSelectedSearchCategoriesDescriptions());
				}
			}
		});

		return searchCategoryBox;
	}

	private List<String> getSelectedSearchCategoriesDescriptions() {

		List<String> selectedSearchCategories = new LinkedList<>();

		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child instanceof SearchCategoryBox) {
				SearchCategoryBox searchCategoryBox = (SearchCategoryBox) child;
				if (searchCategoryBox.isChecked()) {
					if (searchCategoryBox.getSearchCategory().getName().equals(getContext().getString(R.string.other))) {
						for (int j = maxDisplayedCategories - 1; j < searchCategories.size(); j++) {
							selectedSearchCategories.add(searchCategories.get(j).getName());
						}
					} else {
						selectedSearchCategories.add(searchCategoryBox.getSearchCategory().getName());
					}
				}
			}
		}

		return selectedSearchCategories;
	}

	public void setOnFilterSelectedListener(OnFilterSelectedListener onFilterSelectedListener) {
		this.onFilterSelectedListener = onFilterSelectedListener;
	}

	public void checkSearchCategory(String searchCategoryDescription) {
		// In case we got a category which is a part of the "other" category, check the "other" category instead
		SearchCategory searchCategory = getSearchCategoryByDescription(searchCategories, searchCategoryDescription);
		if (searchCategory != null && searchCategories.indexOf(searchCategory) >= maxDisplayedCategories) {
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
				if (searchCategoryBox.getSearchCategory().getName().equals(searchCategoryDescription)) {
					searchCategoryBox.check();
					break;
				}
			}
		}
	}

	private SearchCategory getSearchCategoryByDescription(List<SearchCategory> searchCategories, final String searchCategory) {
		return CollectionUtils.findFirst(searchCategories, new CollectionUtils.Predicate<SearchCategory>() {
			@Override
			public boolean where(SearchCategory item) {
				return item.getName().equals(searchCategory);
			}
		});
	}

	public interface OnFilterSelectedListener {
		void onFilterSelected(List<String> selectedSearchCategories);
	}
}
