package amai.org.conventions.events.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

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
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import sff.org.conventions.R;

public class ProgrammeSearchActivity extends NavigationActivity {

	private static final String STATE_KEYWORDS_FILTER = "KeywordsFilter";
	private static final String STATE_SEARCH_FILTERS = "SearchFilters";

	// Not using the interface List since we want to persist this in the savedInstanceState
	private HashSet<SearchFilter> searchFilters;

	private String keywordsFilter;
	private EventsViewListAdapter adapter;
	private StickyListHeadersListView listView;
	private TextView noResultsFoundView;
	private DrawerLayout drawerLayout;
	private ImageButton filterButton;

	private int totalEventTypeSearchFiltersCount;
	private int totalCategorySearchFiltersCount;
	private int totalTagSearchFiltersCount;

	private AsyncTask<Void, Void, List<ConventionEvent>> currentFilterProcessingTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = setContentInContentContainer(R.layout.activity_programme_search);
		setToolbarTitle(getResources().getString(R.string.programme_search_title));

		if (savedInstanceState != null) {
			Serializable savedFilters = savedInstanceState.getSerializable(STATE_SEARCH_FILTERS);
			if (savedFilters instanceof HashSet) {
				//noinspection unchecked
				searchFilters = (HashSet<SearchFilter>) savedFilters;
			} else {
				searchFilters = new HashSet<>();
			}

			keywordsFilter = savedInstanceState.getString(STATE_KEYWORDS_FILTER);
		} else {
			searchFilters = new HashSet<>();
		}

		noResultsFoundView = (TextView) findViewById(R.id.search_no_results_found);
		drawerLayout = (DrawerLayout) findViewById(R.id.search_drawer_layout);

		initializeEventsList();
		initializeKeywordFilter();

		applyFiltersInBackground();

		filterButton = (ImageButton) findViewById(R.id.search_filter_button);
		refreshFilterButton();

		SearchFilter soldOutFilter = new SearchFilter().withName(getString(R.string.show_sold_out_events)).withType(SearchFilter.Type.Tickets);

		List<SearchFilter> eventTypesSearchFilters = Convention.getInstance().getEventTypesSearchFilters();
		totalEventTypeSearchFiltersCount = eventTypesSearchFilters.size();

		List<SearchFilter> categoryFilters = Convention.getInstance().getCategorySearchFilters();
		totalCategorySearchFiltersCount = categoryFilters.size();

		List<SearchFilter> tagFilters = Convention.getInstance().getKeywordsSearchFilters();
		totalTagSearchFiltersCount = tagFilters.size();

		if (searchFilters.size() == 0) {
			searchFilters.add(soldOutFilter);
			searchFilters.addAll(eventTypesSearchFilters);
			searchFilters.addAll(categoryFilters);
			searchFilters.addAll(tagFilters);
		}

		List<SearchFilter> sortedFilters = new ArrayList<>(searchFilters);
		Collections.sort(sortedFilters, new Comparator<SearchFilter>() {
			@Override
			public int compare(SearchFilter filter, SearchFilter other) {
				if (filter.getType().equals(other.getType())) {
					return filter.getName().compareTo(other.getName());
				}

				return filter.getType().ordinal() - other.getType().ordinal();
			}
		});

		final SearchFiltersAdapter searchFiltersAdapter = new SearchFiltersAdapter(sortedFilters);
		searchFiltersAdapter.setOnFilterChangeListener(new SearchFiltersAdapter.OnFilterChangeListener() {
					@Override
					public void onFilterStateChanged(SearchFilter searchFilter) {
						searchFilters.add(searchFilter);
						applyFiltersInBackground();
					}
				});

		StickyGridHeadersGridView searchFiltersList = (StickyGridHeadersGridView) findViewById(R.id.search_filters_list);
		searchFiltersList.setAreHeadersSticky(false);
		searchFiltersList.setNumColumns(2);
		searchFiltersList.setAdapter(searchFiltersAdapter);

		final Button editAllButton = (Button) findViewById(R.id.search_filter_drawer_container_edit_all_button);
		editAllButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (editAllButton.getText().equals(getResources().getString(R.string.search_filter_select_all))) {
					for (SearchFilter filter : searchFilters) {
						filter.withActive(false);
					}

					editAllButton.setText(getResources().getString(R.string.search_filter_clear_all));
				} else {
					for (SearchFilter filter : searchFilters) {
						filter.withActive(true);
					}

					editAllButton.setText(getResources().getString(R.string.search_filter_select_all));
				}

				searchFiltersAdapter.notifyDataSetChanged();
				applyFiltersInBackground();
			}
		});


		Views.hideKeyboardOnClickOutsideEditText(this, rootView);
	}

	private void refreshFilterButton() {
		int numberOfActiveFilters = CollectionUtils.filter(new ArrayList<>(searchFilters), new CollectionUtils.Predicate<SearchFilter>() {
			@Override
			public boolean where(SearchFilter item) {
				return item.isActive();
			}
		}).size();

		// In case all (or none) of the filters are active, show an empty filter icon (since we don't apply any filters in such cases)
		Drawable filterIcon = getResources().getDrawable(numberOfActiveFilters == 0
				|| numberOfActiveFilters == totalCategorySearchFiltersCount + totalEventTypeSearchFiltersCount + totalTagSearchFiltersCount
				? R.drawable.filter
				: R.drawable.filter_full);
		filterIcon.mutate();
		filterIcon.setColorFilter(ThemeAttributes.getColor(this, R.attr.colorAccent), PorterDuff.Mode.SRC_ATOP);
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
		switch (item.getItemId()) {
			case R.id.programme_search_back:
				if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
					drawerLayout.closeDrawer(GravityCompat.END);
					return true;
				}

				supportFinishAfterTransition();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void initializeEventsList() {
		listView = (StickyListHeadersListView) findViewById(R.id.searchEventsList);

		boolean showHeaders = Convention.getInstance().getLengthInDays() > 1;
		adapter = new EventsViewListAdapter(Collections.<ConventionEvent>emptyList(), listView, showHeaders);
		listView.setAdapter(adapter);
	}

	private void initializeKeywordFilter() {
		EditText keywordTextBox = (EditText) findViewById(R.id.search_keyword_text_box);
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
	}

	private void applyFiltersInBackground() {
		// Duplicating the lists since we will now access it from multiple threads
		final List<SearchFilter> activeFilters = CollectionUtils.filter(new ArrayList<>(searchFilters), new CollectionUtils.Predicate<SearchFilter>() {
			@Override
			public boolean where(SearchFilter item) {
				return item.isActive();
			}
		});

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
				setNoResultsVisibility(adapter.getCount());

				refreshFilterButton();
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private List<ConventionEvent> filterEvents(final String keywordsFilter, final List<SearchFilter> filters) {
		List<ConventionEvent> events = Convention.getInstance().getEvents();

		events = CollectionUtils.filter(events, new CollectionUtils.Predicate<ConventionEvent>() {
			@Override
			public boolean where(ConventionEvent event) {
				boolean result = true;
				if (keywordsFilter != null && keywordsFilter.length() > 0) {
					result = containsKeywords(event);
				}

				SearchFilter soldOutTicketsFilter = CollectionUtils.findFirst(filters, new CollectionUtils.Predicate<SearchFilter>() {
					@Override
					public boolean where(SearchFilter item) {
						return item.getType() == SearchFilter.Type.Tickets;
					}
				});
				// If the filter is active the user doesn't want to show sold out events
				if (soldOutTicketsFilter != null) {
					result &= event.getAvailableTickets() != 0; // tickets<0 means there is no info about the number of tickets
				}

				List<SearchFilter> eventTypeFilters = CollectionUtils.filter(filters, new CollectionUtils.Predicate<SearchFilter>() {
					@Override
					public boolean where(SearchFilter filter) {
						return filter.getType() == SearchFilter.Type.EventType;
					}
				});
				List<EventType> eventTypes = CollectionUtils.map(eventTypeFilters, new CollectionUtils.Mapper<SearchFilter, EventType>() {
					@Override
					public EventType map(SearchFilter item) {
						return new EventType(item.getName());
					}
				});
				if (eventTypes.size() > 0 && eventTypes.size() < totalEventTypeSearchFiltersCount) {
					result &= !eventTypes.contains(event.getType());
				}

				List<SearchFilter> categoryFilters = CollectionUtils.filter(filters, new CollectionUtils.Predicate<SearchFilter>() {
					@Override
					public boolean where(SearchFilter filter) {
						return filter.getType() == SearchFilter.Type.Category;
					}
				});
				List<String> categories = CollectionUtils.map(categoryFilters, new CollectionUtils.Mapper<SearchFilter, String>() {
					@Override
					public String map(SearchFilter item) {
						return item.getName();
					}
				});
				if (categories.size() > 0 && categories.size() < totalCategorySearchFiltersCount) {
					result &= !categories.contains(event.getCategory());
				}

				List<SearchFilter> tagFilters = CollectionUtils.filter(filters, new CollectionUtils.Predicate<SearchFilter>() {
					@Override
					public boolean where(SearchFilter filter) {
						return filter.getType() == SearchFilter.Type.Tag;
					}
				});
				List<String> tags = CollectionUtils.map(tagFilters, new CollectionUtils.Mapper<SearchFilter, String>() {
					@Override
					public String map(SearchFilter item) {
						return item.getName();
					}
				});
				if (tags.size() > 0 && tags.size() < totalTagSearchFiltersCount) {
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
		});
		Collections.sort(events, new ConventionEventComparator());
		if (keywordsFilter != null) {
			adapter.setKeywordsHighlighting(Arrays.asList(keywordsFilter.split(" ")));
		}
		return events;
	}

	private void setNoResultsVisibility(int resultsNumber) {
		// Show the "no results found" message if there are no results after applying the filters
		if (resultsNumber == 0) {
			noResultsFoundView.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		} else {
			noResultsFoundView.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
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
