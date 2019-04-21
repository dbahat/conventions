package amai.org.conventions.secondhand;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ListViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.SecondHandBuy;
import amai.org.conventions.model.SecondHandItem;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;
import sff.org.conventions.R;

public class SecondHandBuyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SecondHandActivity.OnFragmentSelectedListener {

	private static final String STATE_PRICE_FILTER = "priceFilter";
	private static final String STATE_KEYWORD_FILTER = "keywordFilter";
	private static final String STATE_SORT_TYPE = "sortType";
	private static final String STATE_FAVORITES_FILTER = "showFavoritesFilter";

	private SecondHandBuy secondHandBuy;
	private SecondHandSearchItemsAdapter adapter;

	private String keywordsFilter;
	private String priceFilter;
	private SortType sortType;

	private List<String> categoriesFilter;
	private boolean showAllFavorites;
	private AsyncTask<Void, Void, List<SecondHandItem>> currentFilterProcessingTask;

	private SwipeRefreshLayout swipeRefreshLayout;
	private ListView listView;
	private TextView searchResultsNumber;
	private TextView noResultsFoundView;
	private LinearLayout resultsContainer;
	private TextView lastUpdate;
	private TextView sortByName;
	private TextView sortByPrice;
//	private TextView categoriesButton;
//	private TextView categoriesText;


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		secondHandBuy = Convention.getInstance().getSecondHandBuy();

		// Restore state
		if (savedInstanceState != null) {
			keywordsFilter = savedInstanceState.getString(STATE_KEYWORD_FILTER);
			priceFilter = savedInstanceState.getString(STATE_PRICE_FILTER);
			sortType = (SortType) savedInstanceState.getSerializable(STATE_SORT_TYPE);
			showAllFavorites = savedInstanceState.getBoolean(STATE_FAVORITES_FILTER);
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_second_hand_buy, container, false);

		swipeRefreshLayout = view.findViewById(R.id.second_hand_buy_swipe_layout);
		listView = view.findViewById(R.id.second_hand_buy_items_list);
		searchResultsNumber = view.findViewById(R.id.second_hand_buy_search_results_number);
		noResultsFoundView = view.findViewById(R.id.second_hand_buy_no_results_found);
		resultsContainer = view.findViewById(R.id.second_hand_buy_results_container);
		lastUpdate = view.findViewById(R.id.second_hand_buy_items_last_update);
		adapter = new SecondHandSearchItemsAdapter(Collections.<SecondHandItem>emptyList(), secondHandBuy);
		listView.setAdapter(adapter);
		// This is necessary because for some reason the swipe refresh layout here doesn't recognize that
		// the sticky headers list view can scroll up, and when scrolling up it always appears which is annoying
		swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
			@Override
			public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
				return ListViewCompat.canScrollList(listView, -1);
			}
		});

		swipeRefreshLayout.setColorSchemeColors(ThemeAttributes.getColor(getActivity(), R.attr.swipeToRefreshColor));
		swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ThemeAttributes.getColor(getActivity(), R.attr.swipeToRefreshBackgroundColor));
		swipeRefreshLayout.setOnRefreshListener(this);
		updateRefreshTime();

		initializeFiltersAndSort(view);
//		initializeCategories(view);

		refreshItemsListInBackground(false);

		return view;
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(STATE_PRICE_FILTER, priceFilter);
		outState.putString(STATE_KEYWORD_FILTER, keywordsFilter);
		outState.putSerializable(STATE_SORT_TYPE, sortType);
		outState.putBoolean(STATE_FAVORITES_FILTER, showAllFavorites);
	}

	private void initializeFiltersAndSort(View view) {
		EditText keywordTextBox = view.findViewById(R.id.second_hand_buy_name_text_box);
		keywordTextBox.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				keywordsFilter = s.toString();
				applyFiltersInBackground();
			}
		});

		if (keywordsFilter != null) {
			keywordTextBox.setText(keywordsFilter);
		}

		EditText priceTextBox = view.findViewById(R.id.second_hand_buy_price_text_box);
		priceTextBox.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				priceFilter = s.toString();
				applyFiltersInBackground();
			}
		});

		if (priceFilter != null) {
			priceTextBox.setText(priceFilter);
		}

		if (sortType == null) {
			sortType = SortType.BY_NAME;
		}

		sortByName = view.findViewById(R.id.second_hand_buy_sort_by_name_button);
		sortByPrice = view.findViewById(R.id.second_hand_buy_sort_by_price_button);

		sortByName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (sortType == SortType.BY_NAME) {
					return;
				}
				sortType = SortType.BY_NAME;
				updateSortButtonsColor();
				applySortInBackground();
			}
		});

		sortByPrice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (sortType == SortType.BY_PRICE) {
					return;
				}
				sortType = SortType.BY_PRICE;
				updateSortButtonsColor();
				applySortInBackground();
			}
		});
		updateSortButtonsColor();
	}

	private void updateSortButtonsColor() {
		int selectedColor = ThemeAttributes.getColor(getActivity(), R.attr.secondHandSelectedFilterColor);
		int unselectedColor = ThemeAttributes.getColor(getActivity(), R.attr.secondHandFilterColor);
		if (sortType == SortType.BY_NAME) {
			setColor(sortByName, selectedColor);
			setColor(sortByPrice, unselectedColor);
		} else {
			setColor(sortByPrice, selectedColor);
			setColor(sortByName, unselectedColor);
		}
	}

//	private void initializeCategories(View view) {
//		categoriesButton = view.findViewById(R.id.second_hand_buy_categories_button);
//		categoriesText = view.findViewById(R.id.second_hand_buy_selected_categories);
//		updateCategoriesText();
//	}

//	private void updateCategoriesText() {
//		String categories = "";
//		if (showAllFavorites) {
//			categories = getString(R.string.second_hand_favorites);
//			if (categoriesFilter.size() > 0) {
//				categories += ", ";
//			}
//		}
//		categories += TextUtils.join(", ", categoriesFilter);
//		categoriesText.setText(categories);
//	}

	private void setColor(TextView button, int color) {
		button.setTextColor(color);
		for (Drawable drawable : button.getCompoundDrawablesRelative()) {
			if (drawable != null) {
				drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
			}
		}
	}

	@Override
	public void onFragmentSelected(SecondHandActivity context) {
		context.removeActionButton();
	}

	@Override
	public void onRefresh() {
		refreshItemsListInBackground(true);
	}

	private void refreshItemsListInBackground(final boolean force) {
		swipeRefreshLayout.setRefreshing(true);

		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				return secondHandBuy.refresh(force);
			}

			@Override
			protected void onPostExecute(Boolean success) {
				if (!success) {
					Toast.makeText(getContext(), R.string.update_refresh_failed, Toast.LENGTH_SHORT).show();
				}
				// Even if the refresh wasn't successful we still need to set the items, apply the filters etc
				updateRefreshTime();
				swipeRefreshLayout.setRefreshing(false);
				applyFiltersInBackground();
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void applyFiltersInBackground() {
		// Canceling the previous async task so the UI won't be refreshed with outdated search results in case the user
		// is in the middle of typing. Since we use a thread pool, this can also result in the user seeing wrong results
		// (in case an outdated filtering task finishes after the latest one).
		if (currentFilterProcessingTask != null && currentFilterProcessingTask.getStatus() != AsyncTask.Status.FINISHED) {
			currentFilterProcessingTask.cancel(false);
		}

		currentFilterProcessingTask = new AsyncTask<Void, Void, List<SecondHandItem>>() {
			@Override
			protected List<SecondHandItem> doInBackground(Void... params) {
				// TODO clone the list if necessary
				List<SecondHandItem> filteredItems = filterItems(keywordsFilter, priceFilter, categoriesFilter, showAllFavorites);
				applySort(filteredItems);
				return filteredItems;
			}

			@Override
			protected void onPostExecute(List<SecondHandItem> items) {
				adapter.setItems(items);
				updateSearchResultsNumber(adapter.getCount());
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void applySortInBackground() {
		new AsyncTask<Void, Void, List<SecondHandItem>>() {
			@Override
			protected List<SecondHandItem> doInBackground(Void... params) {
				List<SecondHandItem> items = adapter.getItems();
				applySort(items);
				return items;
			}

			@Override
			protected void onPostExecute(List<SecondHandItem> items) {
				adapter.setItems(items);
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void applySort(List<SecondHandItem> items) {
		final SortType sortType = this.sortType;
		Collections.sort(items, new Comparator<SecondHandItem>() {
			@Override
			public int compare(SecondHandItem item1, SecondHandItem item2) {
				// Sort order:
				// 1. if showAllFavorites is selected, favorites are first
				// 2. Sort type - name or price (according to the selected sort type)
				// 3. Item ID (to keep a consistent order, since it's unique)
				int result = 0;
				if (showAllFavorites) {
					// Favorites are on top if selected
					if (secondHandBuy.isFavorite(item1) && !secondHandBuy.isFavorite(item2)) {
						result = 1;
					} else if (!secondHandBuy.isFavorite(item1) && secondHandBuy.isFavorite(item2)) {
						result = -1;
					}
				}
				if (result != 0) {
					return result;
				}

				if (sortType == SortType.BY_NAME) {
					String desc1 = item1.getDescription() == null ? "" : item1.getDescription();
					String desc2 = item2.getDescription() == null ? "" : item2.getDescription();
					result = desc1.compareTo(desc2);
				} else {
					result = item1.getPrice() - item2.getPrice();
				}
				if (result != 0) {
					return result;
				}

				return item1.getId().compareTo(item2.getId());
			}
		});
	}

	private void updateSearchResultsNumber(int count) {
		if (count == 0) {
			resultsContainer.setVisibility(View.GONE);
			listView.setVisibility(View.GONE);
			noResultsFoundView.setVisibility(View.VISIBLE);
		} else {
			if (count == 1) {
				searchResultsNumber.setText(getString(R.string.second_hand_buy_one_search_results_number));
			} else {
				searchResultsNumber.setText(getString(R.string.second_hand_buy_search_results_number, count));
			}
			resultsContainer.setVisibility(View.VISIBLE);
			listView.setVisibility(View.VISIBLE);
			noResultsFoundView.setVisibility(View.GONE);
		}
	}

	private void updateRefreshTime() {
		Date lastRefreshTime = ConventionsApplication.settings.getLastSecondHandSearchItemsUpdateDate();
		if (lastRefreshTime == null) {
			lastUpdate.setText("");
			return;
		}

		String formattedLastModifiedDate;
		if (Dates.isSameDate(lastRefreshTime, Dates.now())) {
			formattedLastModifiedDate = Dates.formatHoursAndMinutes(lastRefreshTime);
		} else {
			formattedLastModifiedDate = Dates.formatDateAndTime(lastRefreshTime);
		}
		lastUpdate.setText(getString(R.string.second_hand_buy_last_update_time, formattedLastModifiedDate));
	}

	private List<SecondHandItem> filterItems(final String keywordsFilter, String priceFilter, final List<String> categoriesFilter, final boolean showAllFavorites) {
		List<SecondHandItem> items = secondHandBuy.getItems();
		if (items == null || items.isEmpty()) {
			return Collections.emptyList();
		}

		Integer maxPrice = null;
		if (priceFilter != null) {
			try {
				maxPrice = Integer.parseInt(priceFilter.trim());
			} catch (RuntimeException e) {
				// Ignore the filter in this case
			}
		}
		final Integer finalMaxPrice = maxPrice;
		return CollectionUtils.filter(items, new CollectionUtils.Predicate<SecondHandItem>() {
			@Override
			public boolean where(SecondHandItem item) {
				if (showAllFavorites && secondHandBuy.isFavorite(item)) {
					return true;
				}
				if (finalMaxPrice != null && item.getPrice() > finalMaxPrice) {
					return false;
				}
				if (keywordsFilter != null && !containsKeywords(item, keywordsFilter)) {
					return false;
				}
				if (categoriesFilter != null && !categoriesFilter.contains(item.getType())) {
					return false;
				}
				return true;
			}
		});
	}

	private boolean containsKeywords(SecondHandItem item, String keywordsFilter) {
		boolean result = true;

		// Split the keyword string into words, and search each word with logical AND
		for (String keyword : keywordsFilter.split(" ")) {
			result &= containsKeyword(item, keyword);
		}

		return result;
	}

	private boolean containsKeyword(SecondHandItem item, String keyword) {
		keyword = keyword.toLowerCase();
		return item.getDescription().toLowerCase().contains(keyword) ||
				item.getType().toLowerCase().contains(keyword);
	}

	private enum SortType {
		BY_NAME,
		BY_PRICE;
	}
}
