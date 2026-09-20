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
import amai.org.conventions.events.adapters.EventsViewListAdapter;
import amai.org.conventions.events.adapters.SearchFiltersAdapter;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.SearchFilter;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Views;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import sff.org.conventions.R;

public class ProgrammeSearchActivity extends NavigationActivity {
	// Filter according to the sent set of tags. Other tag filters will be deselected. The object type must be Set<String>.
	public static final String EXTRA_FILTER_BY_TAGS = "ExtraFilterByTags";

	private static final String STATE_KEYWORDS_FILTER = "KeywordsFilter";
	private static final String STATE_SEARCH_FILTERS = "SearchFilters";

	// Not using the interface List since we want to persist this in the savedInstanceState
	private HashSet<SearchFilter<SearchFilter.EventSearchFilterType>> searchFilters;

	private String keywordsFilter;
	private TextView searchResultsNumber;
	private TextView searchFilterResultsNumber;
	private View searchResultsNumberSeparator;
	private EventsViewListAdapter adapter;
	private StickyListHeadersListView listView;
	private TextView noResultsFoundView;
	private DrawerLayout drawerLayout;
	private ImageButton filterButton;
	private RecyclerView searchFiltersList;

	private int totalEventTypeSearchFiltersCount;
	private int totalCategorySearchFiltersCount;
	private int totalTagSearchFiltersCount;
	private int totalEventLocationTypeFiltersCount;

	private AsyncTask<Void, Void, List<ConventionEvent>> currentFilterProcessingTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = setContentInContentContainer(R.layout.activity_programme_search);
		setToolbarTitle(getResources().getString(R.string.programme_search_title));

		Bundle bundle = (savedInstanceState != null ? savedInstanceState : getIntent().getExtras());
		if (bundle != null) {
			Serializable savedFilters = bundle.getSerializable(STATE_SEARCH_FILTERS);
			if (savedFilters instanceof HashSet) {
				//noinspection unchecked
				searchFilters = (HashSet<SearchFilter<SearchFilter.EventSearchFilterType>>) savedFilters;
			} else {
				searchFilters = new HashSet<>();
			}

			keywordsFilter = bundle.getString(STATE_KEYWORDS_FILTER);
		} else {
			searchFilters = new HashSet<>();
		}

		noResultsFoundView = findViewById(R.id.search_no_results_found);
		searchResultsNumber = findViewById(R.id.search_results_number);
		searchResultsNumberSeparator = findViewById(R.id.search_results_number_separator);
		drawerLayout = findViewById(R.id.search_drawer_layout);

		initializeEventsList();
		initializeKeywordFilter();
		initializeSearchFilters();

		// This must be done after the search filters are initialized
		if (bundle != null && bundle.get(EXTRA_FILTER_BY_TAGS) instanceof Set) {
			Set<String> filterByTags = (Set<String>) bundle.get(EXTRA_FILTER_BY_TAGS);
			for (SearchFilter<SearchFilter.EventSearchFilterType> filter : searchFilters) {
				if (filter.getType() == SearchFilter.EventSearchFilterType.Tag) {
					// Active tag filters filter out. We want to set all the tag filters to active except
					// the sent ones.
					filter.withActive(!filterByTags.contains(filter.getName()));
				}
			}
		}

		applyFiltersInBackground();

		searchFilterResultsNumber = findViewById(R.id.search_filter_results_number);
		filterButton = findViewById(R.id.search_filter_button);
		refreshFilterButton();

		Views.hideKeyboardOnClickOutsideEditText(this, rootView);
		// Handle edge to edge
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, false, findViewById(R.id.programme_search_title_section));
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, Views.InsetType.PADDING, false, listView);
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.PADDING, false, findViewById(R.id.search_filter_drawer_container));
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.NONE, Views.InsetType.NONE, false, searchFiltersList);
	}

	private void initializeSearchFilters() {
		List<SearchFilter<SearchFilter.EventSearchFilterType>> eventTypesSearchFilters = Convention.getInstance().getEventTypesSearchFilters();
		totalEventTypeSearchFiltersCount = eventTypesSearchFilters.size();

		List<SearchFilter<SearchFilter.EventSearchFilterType>> categoryFilters = Convention.getInstance().getCategorySearchFilters();
		totalCategorySearchFiltersCount = categoryFilters.size();

		List<SearchFilter<SearchFilter.EventSearchFilterType>> tagFilters = Convention.getInstance().getKeywordsSearchFilters();
		totalTagSearchFiltersCount = tagFilters.size();

		List<SearchFilter<SearchFilter.EventSearchFilterType>> eventLocationTypeFilters = Convention.getInstance().getEventLocationTypeFilters(getResources());
		// Only show this filter if there is more than 1 event location type
		totalEventLocationTypeFiltersCount = eventLocationTypeFilters.size() > 1 ? eventLocationTypeFilters.size() : 0;

		if (searchFilters.isEmpty()) {
			if (hasEventsWithTicketsInfo()) {
				SearchFilter<SearchFilter.EventSearchFilterType> soldOutFilter = new SearchFilter<SearchFilter.EventSearchFilterType>().withName(getString(R.string.show_sold_out_events)).withType(SearchFilter.EventSearchFilterType.Tickets);
				searchFilters.add(soldOutFilter);
			}
			if (eventLocationTypeFilters.size() > 1) {
				searchFilters.addAll(eventLocationTypeFilters);
			}
			searchFilters.addAll(eventTypesSearchFilters);
			searchFilters.addAll(categoryFilters);
			searchFilters.addAll(tagFilters);
		}

		List<SearchFilter<SearchFilter.EventSearchFilterType>> sortedFilters = new ArrayList<>(searchFilters);
		Collections.sort(sortedFilters, (filter, other) -> {
			if (filter.getType().equals(other.getType())) {
				return filter.getName().compareTo(other.getName());
			}

			return filter.getType().ordinal() - other.getType().ordinal();
		});

		searchFiltersList = findViewById(R.id.search_filters_list);
		searchFiltersList.setLayoutManager(new GridLayoutManager(this, 2));

		final SearchFiltersAdapter<SearchFilter.EventSearchFilterType> searchFiltersAdapter = new SearchFiltersAdapter<>(sortedFilters);
		searchFiltersAdapter.setOnFilterChangeListener(searchFilter -> {
			searchFilters.add(searchFilter);
			applyFiltersInBackground();
		});

		searchFiltersList.setAdapter(new SectionedGridRecyclerViewAdapterWrapper<>(searchFiltersList, searchFiltersAdapter));

		final Button editAllButton = findViewById(R.id.search_filter_drawer_container_edit_all_button);
		editAllButton.setOnClickListener(view -> {
			if (editAllButton.getText().equals(getResources().getString(R.string.search_filter_select_all))) {
				for (SearchFilter<SearchFilter.EventSearchFilterType> filter : searchFilters) {
					filter.withActive(filter.isDisplayActiveAsChecked());
				}

				editAllButton.setText(getResources().getString(R.string.search_filter_clear_all));
			} else {
				for (SearchFilter<SearchFilter.EventSearchFilterType> filter : searchFilters) {
					filter.withActive(!filter.isDisplayActiveAsChecked());
				}

				editAllButton.setText(getResources().getString(R.string.search_filter_select_all));
			}

			searchFiltersAdapter.notifyDataSetChanged();
			applyFiltersInBackground();
		});
	}

	private boolean hasEventsWithTicketsInfo() {
		return CollectionUtils.findFirst(Convention.getInstance().getEvents(), item -> item.getAvailableTickets() >= 0) != null;
	}

	private void refreshFilterButton() {
		List<SearchFilter<SearchFilter.EventSearchFilterType>> activeFilters = CollectionUtils.filter(new ArrayList<>(searchFilters), SearchFilter::isActive);

		boolean areAnyFiltersApplied =
			// The following checks are also done in filterEvents
			filterBySoldOut(activeFilters) ||
				filterByEventLocationTypes(activeFilters) != null ||
				filterByEventType(activeFilters) != null ||
				filterByEventCategory(activeFilters) != null ||
				filterByEventTags(activeFilters) != null;

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
		getMenuInflater().inflate(R.menu.menu_programme_search, menu);

		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(STATE_KEYWORDS_FILTER, keywordsFilter);
		outState.putSerializable(STATE_SEARCH_FILTERS, searchFilters);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return handleOptionsItem(item, Map.of(
			R.id.programme_search_back, () -> {
				if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
					drawerLayout.closeDrawer(GravityCompat.END);
					return;
				}

				onFinishing();
				supportFinishAfterTransition();
			}
		));
	}

	private void initializeEventsList() {
		listView = findViewById(R.id.searchEventsList);

		boolean showHeaders = Convention.getInstance().getLengthInDays() > 1;
		adapter = new EventsViewListAdapter(Collections.emptyList(), listView, showHeaders);
		listView.setAdapter(adapter);
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
		final List<SearchFilter<SearchFilter.EventSearchFilterType>> activeFilters = CollectionUtils.filter(new ArrayList<>(searchFilters), SearchFilter::isActive);

		// Canceling the previous async task so the UI won't be refreshed with outdated search results in case the user
		// is in the middle of typing. Since we use a thread pool, this can also result in the user seeing wrong results
		// (in case an outdated filtering task finish after the latest one).
		if (currentFilterProcessingTask != null && currentFilterProcessingTask.getStatus() != AsyncTask.Status.FINISHED) {
			currentFilterProcessingTask.cancel(false);
		}

		currentFilterProcessingTask = new AsyncTask<Void, Void, List<ConventionEvent>>() {
			@Override
			protected List<ConventionEvent> doInBackground(Void... params) {
				return filterEvents(keywordsFilter, activeFilters);
			}

			@Override
			protected void onPostExecute(List<ConventionEvent> events) {
				adapter.setItems(events);
				updateSearchResultsNumber(adapter.getCount());

				refreshFilterButton();
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private List<ConventionEvent> filterEvents(final String keywordsFilter, final List<SearchFilter<SearchFilter.EventSearchFilterType>> filters) {
		List<ConventionEvent> events = Convention.getInstance().getEvents();

		events = CollectionUtils.filter(events, new CollectionUtils.Predicate<ConventionEvent>() {
			@Override
			public boolean where(ConventionEvent event) {
				// filters - list of currently active filters (active filter = unchecked)
				// result - should we keep the event
				boolean result = true;
				if (keywordsFilter != null && !keywordsFilter.isEmpty()) {
					result = containsKeywords(event);
				}

				if (filterBySoldOut(filters)) {
					result &= event.getAvailableTickets() != 0; // tickets<0 means there is no info about the number of tickets
				}

				List<SearchFilter<SearchFilter.EventSearchFilterType>> eventLocationTypeFilters = filterByEventLocationTypes(filters);
				if (eventLocationTypeFilters != null) {
					List<String> filteredLocationTypes = CollectionUtils.map(eventLocationTypeFilters, SearchFilter::getName);
					List<ConventionEvent.EventLocationType> eventLocationTypes = Convention.getInstance().getEventLocationTypes(event);
					// Only keep events with a location type, if only certain location types are requested
					result &= eventLocationTypes != null && !eventLocationTypes.isEmpty() &&
						// Check if all of event's location types are included in the filters
						!areAllLocationTypesFiltered(eventLocationTypes, filteredLocationTypes);
				}

				List<SearchFilter<SearchFilter.EventSearchFilterType>> eventTypeFilters = filterByEventType(filters);
				if (eventTypeFilters != null) {
					List<EventType> eventTypes = CollectionUtils.map(eventTypeFilters, item -> new EventType(item.getName()));
					result &= !eventTypes.contains(event.getType());
				}

				List<SearchFilter<SearchFilter.EventSearchFilterType>> categoryFilters = filterByEventCategory(filters);
				if (categoryFilters != null) {
					List<String> categories = CollectionUtils.map(categoryFilters, SearchFilter::getName);
					result &= !categories.contains(event.getCategory());
				}

				List<SearchFilter<SearchFilter.EventSearchFilterType>> tagFilters = filterByEventTags(filters);
				if (tagFilters != null) {
					List<String> tags = CollectionUtils.map(tagFilters, SearchFilter::getName);
					result &= !areAllEventTagsFiltered(event, tags);
				}

				return result;
			}

			private boolean areAllEventTagsFiltered(ConventionEvent event, List<String> filteredTags) {
				for (String tag : event.getTags()) {
					if (!filteredTags.contains(tag)) {
						return false;
					}
				}

				return true;
			}

			private boolean areAllLocationTypesFiltered(List<ConventionEvent.EventLocationType> locationTypes, List<String> filteredLocationTypes) {
				for (ConventionEvent.EventLocationType locationType : locationTypes) {
					String locationTypeDesc = getResources().getString(locationType.getDescriptionStringId());
					if (!filteredLocationTypes.contains(locationTypeDesc)) {
						return false;
					}
				}
				return true;
			}
		});
		Collections.sort(events, new ConventionEventComparator());
		if (keywordsFilter != null) {
			adapter.setKeywordsHighlighting(Arrays.asList(keywordsFilter.split(" ")));
		}
		return events;
	}

	private boolean filterBySoldOut(List<SearchFilter<SearchFilter.EventSearchFilterType>> activeFilters) {
		SearchFilter<SearchFilter.EventSearchFilterType> soldOutTicketsFilter = CollectionUtils.findFirst(activeFilters, item -> item.getType() == SearchFilter.EventSearchFilterType.Tickets);
		// If the filter is active the user doesn't want to show sold out events
		return soldOutTicketsFilter != null;
	}

	private List<SearchFilter<SearchFilter.EventSearchFilterType>> filterByEventLocationTypes(List<SearchFilter<SearchFilter.EventSearchFilterType>> activeFilters) {
		if (totalEventLocationTypeFiltersCount > 0) {
			List<SearchFilter<SearchFilter.EventSearchFilterType>> eventLocationTypeFilters = CollectionUtils.filter(activeFilters, filter -> filter.getType() == SearchFilter.EventSearchFilterType.EventLocationType);
			if (!eventLocationTypeFilters.isEmpty() && eventLocationTypeFilters.size() < totalEventLocationTypeFiltersCount) {
				return eventLocationTypeFilters;
			}
		}
		return null;
	}

	private List<SearchFilter<SearchFilter.EventSearchFilterType>> filterByEventType(List<SearchFilter<SearchFilter.EventSearchFilterType>> activeFilters) {
		List<SearchFilter<SearchFilter.EventSearchFilterType>> eventTypeFilters = CollectionUtils.filter(activeFilters, filter -> filter.getType() == SearchFilter.EventSearchFilterType.EventType);
		if (!eventTypeFilters.isEmpty() && eventTypeFilters.size() < totalEventTypeSearchFiltersCount) {
			return eventTypeFilters;
		}
		return null;
	}

	private List<SearchFilter<SearchFilter.EventSearchFilterType>> filterByEventCategory(List<SearchFilter<SearchFilter.EventSearchFilterType>> activeFilters) {
		List<SearchFilter<SearchFilter.EventSearchFilterType>> categoryFilters = CollectionUtils.filter(activeFilters, filter -> filter.getType() == SearchFilter.EventSearchFilterType.Category);
		if (!categoryFilters.isEmpty() && categoryFilters.size() < totalCategorySearchFiltersCount) {
			return categoryFilters;
		}
		return null;
	}

	private List<SearchFilter<SearchFilter.EventSearchFilterType>> filterByEventTags(List<SearchFilter<SearchFilter.EventSearchFilterType>> activeFilters) {
		List<SearchFilter<SearchFilter.EventSearchFilterType>> tagFilters = CollectionUtils.filter(activeFilters, filter -> filter.getType() == SearchFilter.EventSearchFilterType.Tag);
		if (!tagFilters.isEmpty() && tagFilters.size() < totalTagSearchFiltersCount) {
			return tagFilters;
		}
		return null;
	}

	private void updateSearchResultsNumber(int resultsNumber) {
		// Show the "no results found" message if there are no results after applying the filters
		if (resultsNumber == 0) {
			noResultsFoundView.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			searchResultsNumberSeparator.setVisibility(View.GONE);
			searchResultsNumber.setVisibility(View.GONE);
			searchFilterResultsNumber.setText(getString(R.string.no_events_found));
		} else {
			noResultsFoundView.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			searchResultsNumberSeparator.setVisibility(View.VISIBLE);
			searchResultsNumber.setVisibility(View.VISIBLE);
			String searchResultsText;
			if (resultsNumber == 1) {
				searchResultsText = getString(R.string.events_one_search_results_number);
			} else {
				searchResultsText = getString(R.string.events_search_results_number, resultsNumber);
			}
			searchResultsNumber.setText(searchResultsText);
			searchFilterResultsNumber.setText(searchResultsText);
		}
	}

	private boolean containsKeywords(ConventionEvent event) {
		boolean result = true;

		// Split the keyword string into words, and search each word with logical AND
		for (String keyword : keywordsFilter.split(" ")) {
			result &= containsKeyword(event, keyword);
		}

		return result;
	}

	private boolean containsKeyword(ConventionEvent event, String keyword) {
		// Filter out HTML tags from the event description
		String filteredEventDescription = event.getPlainTextDescription();

		keyword = keyword.toLowerCase();
		return event.getTitle().toLowerCase().contains(keyword)
				|| (event.getSubTitle() != null && event.getSubTitle().toLowerCase().contains(keyword))
				|| event.getLecturer().toLowerCase().contains(keyword)
				|| event.getHall().getName().toLowerCase().contains(keyword)
				|| filteredEventDescription.toLowerCase().contains(keyword)
				|| event.getTagsAsString().toLowerCase().contains(keyword);
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
		// (including going into an event, adding it to favorites and then returning)
		adapter.notifyDataSetChanged();
	}
}
