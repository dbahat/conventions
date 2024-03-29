package amai.org.conventions.events.activities;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.ChipCategoriesLayout;
import amai.org.conventions.events.adapters.EventsViewListAdapter;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Views;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProgrammeSearchActivity extends NavigationActivity {

	private static final String STATE_KEYWORDS_FILTER = "KeywordsFilter";
	private static final String STATE_EVENT_TYPES_FILTER = "EventTypesFilter";
	public static final int NO_DELAY = 0;
	public static final int CHECKBOX_ANIMATION_TIME = 200;

	private LinkedList<EventType> eventTypeFilter;
	private String keywordsFilter;
	private EventsViewListAdapter adapter;
	private StickyListHeadersListView listView;
	private TextView noResultsFoundView;
	private TextView searchResultsNumber;
	private View searchResultsNumberSeparator;

	private AsyncTask<Void, Void, List<ConventionEvent>> currentFilterProcessingTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = setContentInContentContainer(R.layout.activity_programme_search);
		setToolbarTitle(getResources().getString(R.string.programme_search_title));

		if (savedInstanceState != null) {
			Serializable savedEventTypes = savedInstanceState.getSerializable(STATE_EVENT_TYPES_FILTER);
			if (savedEventTypes instanceof LinkedList && CollectionUtils.contains((List) savedEventTypes, EventType.class)) {
				//noinspection unchecked
				eventTypeFilter = (LinkedList<EventType>) savedEventTypes;
			} else {
				eventTypeFilter = new LinkedList<>();
			}
			keywordsFilter = savedInstanceState.getString(STATE_KEYWORDS_FILTER);
		} else {
			eventTypeFilter = new LinkedList<>();
		}

		searchResultsNumber = findViewById(R.id.search_results_number);
		searchResultsNumberSeparator = findViewById(R.id.search_results_number_separator);
		noResultsFoundView = (TextView) findViewById(R.id.search_no_results_found);

		initializeEventsList();
		initializeKeywordFilter();
		initializeSearchCategories();

		applyFiltersInBackground(NO_DELAY);

		Views.hideKeyboardOnClickOutsideEditText(this, rootView);
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
		outState.putSerializable(STATE_EVENT_TYPES_FILTER, eventTypeFilter);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.programme_search_back:
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

	private void initializeSearchCategories() {
		final ChipCategoriesLayout searchCategoriesLayout = findViewById(R.id.search_categories_layout);
		// Wait for the layout to finish before configuring the checkbox.
		// Seems to be needed since otherwise after config change, the state of the UI components in the layout changes.
		searchCategoriesLayout.post(() -> {
			searchCategoriesLayout.setSearchCategories(Convention.getInstance().getEventTypesSearchCategories(ProgrammeSearchActivity.this));
			searchCategoriesLayout.setOnFilterSelectedListener(selectedSearchCategories -> {
					eventTypeFilter = new LinkedList<>(CollectionUtils.map(selectedSearchCategories, EventType::new));
					// We must delay the UI update here because if adapter.setItems runs during the checkbox
					// toggle animation it makes it not smooth, so we need to ensure it runs after it ends
					applyFiltersInBackground(CHECKBOX_ANIMATION_TIME);
				});

			if (eventTypeFilter.size() > 0) {
				for (EventType eventType : eventTypeFilter) {
					searchCategoriesLayout.checkSearchCategory(eventType.getDescription());
				}
			}
		});
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
				applyFiltersInBackground(NO_DELAY);
			}
		});

		Drawable textEditBackground = ThemeAttributes.getDrawable(this, R.attr.programmeSearchBarBackground);
		if (textEditBackground != null) {
			keywordTextBox.setBackground(textEditBackground);
			keywordTextBox.setBackgroundTintList(null);
		}

		if (keywordsFilter != null) {
			keywordTextBox.setText(keywordsFilter);
		}
	}

	private void applyFiltersInBackground(final long uiUpdateDelayTime) {
		final String keywordsFilter = this.keywordsFilter;
		final LinkedList<EventType> eventTypeFilter = this.eventTypeFilter;

		// Canceling the previous async task so the UI won't be refreshed with outdated search results in case the user
		// is in the middle of typing. Since we use a thread pool, this can also result in the user seeing wrong results
		// (in case an outdated filtering task finish after the latest one).
		if (currentFilterProcessingTask != null && currentFilterProcessingTask.getStatus() != AsyncTask.Status.FINISHED) {
			currentFilterProcessingTask.cancel(false);
		}

		currentFilterProcessingTask = new AsyncTask<Void, Void, List<ConventionEvent>>() {
			@Override
			protected List<ConventionEvent> doInBackground(Void... params) {
				return filterEvents(keywordsFilter, eventTypeFilter);
			}

			@Override
			protected void onPostExecute(final List<ConventionEvent> events) {
				listView.postDelayed(new Runnable() {
					@Override
					public void run() {
						adapter.setItems(events);
						updateSearchResultsNumber(adapter.getCount());
					}
				}, uiUpdateDelayTime);
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private List<ConventionEvent> filterEvents(final String keywordsFilter, final List<EventType> eventTypeFilter) {
		List<ConventionEvent> events = Convention.getInstance().getEvents();

		events = CollectionUtils.filter(events, new CollectionUtils.Predicate<ConventionEvent>() {
			@Override
			public boolean where(ConventionEvent event) {
				boolean result = true;
				if (keywordsFilter != null && keywordsFilter.length() > 0) {
					result = containsKeywords(event);
				}
				if (eventTypeFilter != null && eventTypeFilter.size() > 0) {
					result &= eventTypeFilter.contains(event.getType());
				}
				return result;
			}
		});
		Collections.sort(events, new ConventionEventComparator());
		if (keywordsFilter != null) {
			adapter.setKeywordsHighlighting(Arrays.asList(keywordsFilter.split(" ")));
		}
		return events;
	}

	private void updateSearchResultsNumber(int resultsNumber) {
		// Show the "no results found" message if there are no results after applying the filters
		if (resultsNumber == 0) {
			noResultsFoundView.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			searchResultsNumberSeparator.setVisibility(View.GONE);
			searchResultsNumber.setVisibility(View.GONE);
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
				|| filteredEventDescription.toLowerCase().contains(keyword);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Always redraw the list during onResume, since it's a fast operation, and this ensures the data is up to date in case the activity got paused
		// (including going into an event, adding it to favorites and then returning)
		adapter.notifyDataSetChanged();
	}
}
