package amai.org.conventions.events.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.adapters.SearchFiltersAdapter;
import amai.org.conventions.map.StandSearchViewHolder;
import amai.org.conventions.map.StandsSearchAdapter;
import amai.org.conventions.model.SearchFilter;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandType;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.networking.StandsRefresher;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Views;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import sff.org.conventions.R;

public class StandsSearchActivity extends NavigationActivity {

	public static final String EXTRA_KEYWORDS_FILTER = "ExtraKeywordsFilter";
	private static final String STATE_SEARCH_FILTERS = "SearchFilters";

	public static final String VIEW_NAME_SEARCH_CARD = "search_card";

	// Not using the interface List since we want to persist this in the savedInstanceState
	private HashSet<SearchFilter<SearchFilter.StandSearchFilterType>> searchFilters;

	private String keywordsFilter;
	private TextView searchResultsNumber;
	private TextView searchFilterResultsNumber;
	private View searchResultsNumberSeparator;
	private StandsSearchAdapter adapter;
	private RecyclerView standsList;
	private TextView noResultsFoundView;
	private DrawerLayout drawerLayout;
	private ImageButton filterButton;

	private int totalStandTypeSearchFiltersCount;
	private int totalTagSearchFiltersCount;
	private StandsRefresher.OnRefreshFinishedListener listener;

	private AsyncTask<Void, Void, List<Stand>> currentFilterProcessingTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = setContentInContentContainer(R.layout.activity_stands_search);
		setToolbarTitle(getResources().getString(R.string.stands_search));

		Bundle bundle = (savedInstanceState != null ? savedInstanceState : getIntent().getExtras());
		if (bundle != null) {
			keywordsFilter = bundle.getString(EXTRA_KEYWORDS_FILTER);
			Serializable savedFilters = bundle.getSerializable(STATE_SEARCH_FILTERS);
			if (savedFilters instanceof HashSet) {
				//noinspection unchecked
				searchFilters = (HashSet<SearchFilter<SearchFilter.StandSearchFilterType>>) savedFilters;
			} else {
				searchFilters = new HashSet<>();
			}
		} else {
			searchFilters = new HashSet<>();
		}

		findViewById(R.id.stands_search_card).setTransitionName(VIEW_NAME_SEARCH_CARD);

		noResultsFoundView = (TextView) findViewById(R.id.search_no_results_found);
		searchResultsNumber = findViewById(R.id.search_results_number);
		searchResultsNumberSeparator = findViewById(R.id.search_results_number_separator);
		drawerLayout = (DrawerLayout) findViewById(R.id.search_drawer_layout);

		initializeStandsList();
		initializeKeywordFilter();

		applyFiltersInBackground();

		searchFilterResultsNumber = findViewById(R.id.search_filter_results_number);
		filterButton = findViewById(R.id.search_filter_button);
		refreshFilterButton();

		setupFilters();

		// Handle stands list refresh
		listener = new StandsRefresher.OnRefreshFinishedListener() {
			@Override
			public void onSuccess(boolean force) {
				// Update stands list
				applyFiltersInBackground();
			}
		};
		StandsRefresher.getInstance().addListener(listener);

		Views.hideKeyboardOnClickOutsideEditText(this, rootView);
		// Handle edge to edge
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, false, findViewById(R.id.stands_search_title_section));
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, Views.InsetType.PADDING, false, standsList);
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.PADDING, false, findViewById(R.id.search_filter_drawer_container));
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.NONE, Views.InsetType.NONE, false, findViewById(R.id.search_filters_list));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (listener != null) {
			StandsRefresher.getInstance().removeListener(listener);
		}
	}

	private void setupFilters() {
		List<Stand> stands = Convention.getInstance().getStands();

		List<SearchFilter<SearchFilter.StandSearchFilterType>> typeSearchFilters = CollectionUtils.map(stands, stand ->
			new SearchFilter<SearchFilter.StandSearchFilterType>().withName(stand.getType().getName()).withType(SearchFilter.StandSearchFilterType.Type)
		);
		List<SearchFilter<SearchFilter.StandSearchFilterType>> standTypesSearchFilters = Convention.getInstance().normalizeSearchFilters(typeSearchFilters);
		totalStandTypeSearchFiltersCount = standTypesSearchFilters.size();

		Set<String> allTags = new HashSet<>();
		for (Stand stand : stands) {
			if (stand.getTags() != null) {
				allTags.addAll(stand.getTags());
			}
		}

		List<SearchFilter<SearchFilter.StandSearchFilterType>> tagSearchFilters = CollectionUtils.map(new ArrayList<>(allTags), tag ->
			new SearchFilter<SearchFilter.StandSearchFilterType>().withName(tag).withType(SearchFilter.StandSearchFilterType.Tag)
		);
		List<SearchFilter<SearchFilter.StandSearchFilterType>> tagFilters = Convention.getInstance().normalizeSearchFilters(tagSearchFilters);
		totalTagSearchFiltersCount = tagFilters.size();

		String activeStandsTitle = getString(R.string.show_only_active_stands);
		if (searchFilters.isEmpty()) {
			if (showOnlyActiveStandsCheckbox(stands)) {
				SearchFilter<SearchFilter.StandSearchFilterType> activeStandFilter = new SearchFilter<SearchFilter.StandSearchFilterType>().withName(activeStandsTitle).withType(SearchFilter.StandSearchFilterType.General).withDisplayActiveAsChecked(true);
				searchFilters.add(activeStandFilter);
			}

			if (showOnlyDiscountsStandsCheckbox(stands)) {
				SearchFilter<SearchFilter.StandSearchFilterType> discountsStandFilter = new SearchFilter<SearchFilter.StandSearchFilterType>().withName(getString(R.string.show_only_discount_stands)).withType(SearchFilter.StandSearchFilterType.General).withDisplayActiveAsChecked(true);
				searchFilters.add(discountsStandFilter);
			}

			searchFilters.addAll(standTypesSearchFilters);
			searchFilters.addAll(tagFilters);
		}

		List<SearchFilter<SearchFilter.StandSearchFilterType>> sortedFilters = new ArrayList<>(searchFilters);
		Collections.sort(sortedFilters, (filter, other) -> {
			if (filter.getType().equals(other.getType())) {
				// Active stands filter is first
				if (filter.getName().equals(activeStandsTitle)) {
					return -1;
				}
				return filter.getName().compareTo(other.getName());
			}

			return filter.getType().ordinal() - other.getType().ordinal();
		});

		RecyclerView searchFiltersList = findViewById(R.id.search_filters_list);
		searchFiltersList.setLayoutManager(new GridLayoutManager(this, 2));

		final SearchFiltersAdapter<SearchFilter.StandSearchFilterType> searchFiltersAdapter = new SearchFiltersAdapter<>(sortedFilters);
		searchFiltersAdapter.setOnFilterChangeListener(searchFilter -> {
			searchFilters.add(searchFilter);
			applyFiltersInBackground();
		});

		searchFiltersList.setAdapter(new SectionedGridRecyclerViewAdapterWrapper<>(searchFiltersList, searchFiltersAdapter));

		final Button editAllButton = (Button) findViewById(R.id.search_filter_drawer_container_edit_all_button);
		editAllButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (editAllButton.getText().equals(getResources().getString(R.string.search_filter_select_all))) {
					for (SearchFilter<SearchFilter.StandSearchFilterType> filter : searchFilters) {
						filter.withActive(filter.isDisplayActiveAsChecked());
					}

					editAllButton.setText(getResources().getString(R.string.search_filter_clear_all));
				} else {
					for (SearchFilter<SearchFilter.StandSearchFilterType> filter : searchFilters) {
						filter.withActive(!filter.isDisplayActiveAsChecked());
					}

					editAllButton.setText(getResources().getString(R.string.search_filter_select_all));
				}

				searchFiltersAdapter.notifyDataSetChanged();
				applyFiltersInBackground();
			}
		});
	}

	private boolean showOnlyActiveStandsCheckbox(List<Stand> stands) {
		// Only show during the convention
		if (!Convention.getInstance().hasStarted() || Convention.getInstance().hasEnded()) {
			return false;
		}

		// Only show when at least one stand is active and at least one stand is inactive
		if (stands.isEmpty()) {
			return false;
		}
		boolean initialValue = stands.get(0).isActive();
		for (Stand stand : stands) {
			if (stand.isActive() != initialValue) {
				return true;
			}
		}
		return false;
	}

	private boolean showOnlyDiscountsStandsCheckbox(List<Stand> stands) {
		if (stands.isEmpty()) {
			return false;
		}
		boolean initialValue = stands.get(0).hasDiscount();
		for (Stand stand : stands) {
			if (stand.hasDiscount() != initialValue) {
				return true;
			}
		}
		return false;
	}

	private void refreshFilterButton() {
		List<SearchFilter<SearchFilter.StandSearchFilterType>> activeFilters = CollectionUtils.filter(new ArrayList<>(searchFilters), SearchFilter::isActive);

		boolean areAnyFiltersApplied =
			// The following checks are also done in filterStands
			filterByActiveStand(activeFilters) ||
				filterByDiscount(activeFilters) ||
				filterByStandType(activeFilters) != null ||
				filterByStandTags(activeFilters) != null;

		// In case no filters are applied, show an empty filter icon
		Drawable filterIcon = ContextCompat.getDrawable(this, areAnyFiltersApplied
			? R.drawable.filter_alt_full
			: R.drawable.filter_alt_empty);
		filterIcon.mutate();
		filterIcon.setColorFilter(ThemeAttributes.getColor(this, R.attr.programmeSearchFilterColor), PorterDuff.Mode.SRC_ATOP);
		filterButton.setImageDrawable(filterIcon);
	}

	@Override
	public boolean onCreateCustomOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_stand_search, menu);

		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(EXTRA_KEYWORDS_FILTER, keywordsFilter);
		outState.putSerializable(STATE_SEARCH_FILTERS, searchFilters);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return handleOptionsItem(item, Map.of(
			R.id.stand_search_back, () -> {
				if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
					drawerLayout.closeDrawer(GravityCompat.END);
					return;
				}

				onFinishing();
				supportFinishAfterTransition();
			}
		));
	}

	private void initializeStandsList() {
		standsList = findViewById(R.id.search_stands_list);
		adapter = new StandsSearchAdapter(Collections.emptyList());
		standsList.setLayoutManager(new LinearLayoutManager(this));
		standsList.setAdapter(adapter);
		adapter.setOnClickListener(new StandSearchViewHolder.OnClickListener() {
			@Override
			public void onItemClicked(View standView, Stand stand) {
				StandsArea standsArea = stand.getStandsArea();

				// Open stands area screen with the stand selected
				Bundle bundle = new Bundle();
				bundle.putString(StandsAreaActivity.EXTRA_STANDS_AREA_NAME, standsArea.getName());
				bundle.putString(StandsAreaActivity.EXTRA_STAND_NAME, stand.getName());

				ActivityOptionsCompat transitionOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(StandsSearchActivity.this, standView, StandsAreaActivity.VIEW_NAME_STANDS_LIST);
				navigateToActivity(StandsAreaActivity.class, false, bundle, transitionOptions);
			}

			@Override
			public void onItemInfoClicked(Stand stand, List<String> keywordsToHighlight) {
				StandsAreaActivity.showStandInfo(StandsSearchActivity.this, stand, keywordsToHighlight);
			}
		});
	}

	private void initializeKeywordFilter() {
		SearchView searchView = findViewById(R.id.search_keyword_text_box);
		searchView.setIconified(false);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				keywordsFilter = query;
				applyFiltersInBackground();
				// Returning false closes the text edit when it's full screen and the keyboard when it's not
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				keywordsFilter = newText;
				applyFiltersInBackground();
				return true;
			}
		});

		if (keywordsFilter != null) {
			searchView.setQuery(keywordsFilter, true);
		}
	}

	private void applyFiltersInBackground() {
		// Duplicating the lists since we will now access it from multiple threads
		final List<SearchFilter<SearchFilter.StandSearchFilterType>> activeFilters = CollectionUtils.filter(new ArrayList<>(searchFilters), SearchFilter::isActive);

		// Canceling the previous async task so the UI won't be refreshed with outdated search results in case the user
		// is in the middle of typing. Since we use a thread pool, this can also result in the user seeing wrong results
		// (in case an outdated filtering task finish after the latest one).
		if (currentFilterProcessingTask != null && currentFilterProcessingTask.getStatus() != AsyncTask.Status.FINISHED) {
			currentFilterProcessingTask.cancel(false);
		}

		currentFilterProcessingTask = new AsyncTask<Void, Void, List<Stand>>() {
			@Override
			protected List<Stand> doInBackground(Void... params) {
				return filterStands(keywordsFilter, activeFilters);
			}

			@Override
			protected void onPostExecute(List<Stand> stands) {
				adapter.setStands(stands);
				updateSearchResultsNumber(adapter.getItemCount());

				refreshFilterButton();
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private List<Stand> filterStands(final String keywordsFilter, final List<SearchFilter<SearchFilter.StandSearchFilterType>> filters) {
		List<Stand> stands = Convention.getInstance().getStands();

		stands = CollectionUtils.filter(stands, new CollectionUtils.Predicate<Stand>() {
			@Override
			public boolean where(Stand stand) {
				// filters - list of currently active filters (active filter = unchecked)
				// result - should we keep the event
				boolean result = true;
				if (keywordsFilter != null && !keywordsFilter.isEmpty()) {
					result = containsKeywords(stand);
				}

				if (filterByActiveStand(filters)) {
					result &= stand.isActive();
				}

				if (filterByDiscount(filters)) {
					result &= stand.hasDiscount();
				}

				List<SearchFilter<SearchFilter.StandSearchFilterType>> standTypeFilters = filterByStandType(filters);
				if (standTypeFilters != null) {
					List<String> standTypes = CollectionUtils.map(standTypeFilters, SearchFilter::getName);
					result &= CollectionUtils.findFirst(standTypes, name -> name.equals(stand.getType().getName())) == null;
				}

				List<SearchFilter<SearchFilter.StandSearchFilterType>> tagFilters = filterByStandTags(filters);
				if (tagFilters != null) {
					List<String> tags = CollectionUtils.map(tagFilters, SearchFilter::getName);
					result &= !areAllStandTagsFiltered(stand, tags);
				}

				return result;
			}

			private boolean areAllStandTagsFiltered(Stand stand, List<String> filteredTags) {
				if (stand.getTags() != null) {
					for (String tag : stand.getTags()) {
						if (!filteredTags.contains(tag)) {
							return false;
						}
					}
				}

				return true;
			}
		});
		Collections.sort(stands, (lhs, rhs) -> lhs.getName().compareTo(rhs.getName()));
		if (keywordsFilter != null) {
			adapter.setKeywordsHighlighting(Arrays.asList(keywordsFilter.split(" ")));
		}
		return stands;
	}

	private boolean filterByActiveStand(List<SearchFilter<SearchFilter.StandSearchFilterType>> filters) {
		return CollectionUtils.findFirst(filters, filter -> filter.getType() == SearchFilter.StandSearchFilterType.General && filter.getName().equals(getString(R.string.show_only_active_stands))) != null;
	}

	private boolean filterByDiscount(List<SearchFilter<SearchFilter.StandSearchFilterType>> filters) {
		return CollectionUtils.findFirst(filters, filter -> filter.getType() == SearchFilter.StandSearchFilterType.General && filter.getName().equals(getString(R.string.show_only_discount_stands))) != null;
	}

	private List<SearchFilter<SearchFilter.StandSearchFilterType>> filterByStandType(List<SearchFilter<SearchFilter.StandSearchFilterType>> activeFilters) {
		List<SearchFilter<SearchFilter.StandSearchFilterType>> standTypeFilters = CollectionUtils.filter(activeFilters, filter -> filter.getType() == SearchFilter.StandSearchFilterType.Type);
		if (!standTypeFilters.isEmpty() && standTypeFilters.size() < totalStandTypeSearchFiltersCount) {
			return standTypeFilters;
		}
		return null;
	}

	private List<SearchFilter<SearchFilter.StandSearchFilterType>> filterByStandTags(List<SearchFilter<SearchFilter.StandSearchFilterType>> activeFilters) {
		List<SearchFilter<SearchFilter.StandSearchFilterType>> tagFilters = CollectionUtils.filter(activeFilters, filter -> filter.getType() == SearchFilter.StandSearchFilterType.Tag);
		if (!tagFilters.isEmpty() && tagFilters.size() < totalTagSearchFiltersCount) {
			return tagFilters;
		}
		return null;
	}

	private void updateSearchResultsNumber(int resultsNumber) {
		// Show the "no results found" message if there are no results after applying the filters
		if (resultsNumber == 0) {
			noResultsFoundView.setVisibility(View.VISIBLE);
			standsList.setVisibility(View.GONE);
			searchResultsNumberSeparator.setVisibility(View.GONE);
			searchResultsNumber.setVisibility(View.GONE);
			searchFilterResultsNumber.setText(getString(R.string.no_stands_found));
		} else {
			noResultsFoundView.setVisibility(View.GONE);
			standsList.setVisibility(View.VISIBLE);
			searchResultsNumberSeparator.setVisibility(View.VISIBLE);
			searchResultsNumber.setVisibility(View.VISIBLE);
			String searchResultsText;
			if (resultsNumber == 1) {
				searchResultsText = getString(R.string.stands_one_search_results_number);
			} else {
				searchResultsText = getString(R.string.stands_search_results_number, resultsNumber);
			}
			searchResultsNumber.setText(searchResultsText);
			searchFilterResultsNumber.setText(searchResultsText);
		}
	}

	private boolean containsKeywords(Stand stand) {
		boolean result = true;

		// Split the keyword string into words, and search each word with logical AND
		for (String keyword : keywordsFilter.split(" ")) {
			result &= containsKeyword(stand, keyword);
		}

		return result;
	}

	private boolean containsKeyword(Stand stand, String keywordAnyCase) {
		String searchTerm = keywordAnyCase.toLowerCase();
		return stand.getName().toLowerCase().contains(searchTerm) ||
			(stand.getDescription() != null && stand.getDescription().toLowerCase().contains(searchTerm)) ||
			!CollectionUtils.filter(CollectionUtils.map(stand.getTypes(), StandType::getName), name -> name.toLowerCase().contains(searchTerm)).isEmpty() ||
			(stand.getTags() != null && !CollectionUtils.filter(stand.getTags(), name -> name.toLowerCase().contains(searchTerm)).isEmpty());
	}

	public void onFilterClicked(View view) {
		if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
			drawerLayout.closeDrawer(GravityCompat.END);
		} else {
			drawerLayout.openDrawer(GravityCompat.END);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Always redraw the list during onResume, since it's a fast operation, and this ensures the data is up to date in case the activity got paused
		adapter.notifyDataSetChanged();
	}
}
