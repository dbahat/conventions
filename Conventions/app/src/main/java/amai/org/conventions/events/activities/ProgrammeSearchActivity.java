package amai.org.conventions.events.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.SearchCategoriesLayout;
import amai.org.conventions.events.adapters.SwipeableEventsViewAdapter;
import amai.org.conventions.events.adapters.SwipeableEventsViewListAdapter;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.EventType;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Strings;
import amai.org.conventions.utils.Views;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProgrammeSearchActivity extends NavigationActivity {

    private static final String STATE_KEYWORDS_FILTER = "KeywordsFilter";
    private static final String STATE_EVENT_TYPES_FILTER = "EventTypesFilter";

    private LinkedList<EventType> eventTypeFilter;
    private String keywordsFilter;
    private SwipeableEventsViewListAdapter adapter;
    private StickyListHeadersListView listView;
    private TextView noResultsFoundView;

    private AsyncTask<Void, Void, List<ConventionEvent>> currentFilterProcessingTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = setContentInContentContainer(R.layout.activity_programme_search);
        setToolbarTitle(getResources().getString(R.string.programme_search_title));

        if (savedInstanceState != null) {
	        Serializable savedEventTypes = savedInstanceState.getSerializable(STATE_EVENT_TYPES_FILTER);
	        if (savedEventTypes instanceof LinkedList && CollectionUtils.contains((List)savedEventTypes, EventType.class)) {
                //noinspection unchecked
	            eventTypeFilter = (LinkedList<EventType>) savedEventTypes;
	        }
            keywordsFilter = savedInstanceState.getString(STATE_KEYWORDS_FILTER);
        } else {
            eventTypeFilter = new LinkedList<>();
        }

	    noResultsFoundView = (TextView) findViewById(R.id.search_no_results_found);

        initializeEventsList();
        initializeKeywordFilter();
        initializeSearchCategories();

        applyFiltersInBackground();

        Views.hideKeyboardOnClickOutsideEditText(this, rootView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

        adapter = new SwipeableEventsViewListAdapter(Collections.<ConventionEvent>emptyList(), listView);
        listView.setAdapter(adapter);
    }

    private void initializeSearchCategories() {
        final SearchCategoriesLayout searchCategoriesLayout = (SearchCategoriesLayout) findViewById(R.id.search_categories_layout);
        // Wait for the layout to finish before configuring the checkbox.
        // Seems to be needed since otherwise after config change, the state of the UI components in the layout changes.
        searchCategoriesLayout.post(new Runnable() {
            @Override
            public void run() {
                List<String> searchCategories = CollectionUtils.map(Convention.getInstance().getEventTypes(), new CollectionUtils.Mapper<EventType, String>() {
                    @Override
                    public String map(EventType item) {
                        return item.getDescription();
                    }
                });
                searchCategoriesLayout.setMaxDisplayedCategories(10);
                searchCategoriesLayout.setSearchCategories(searchCategories);
                searchCategoriesLayout.setOnFilterSelectedListener(new SearchCategoriesLayout.OnFilterSelectedListener() {
                    @Override
                    public void onFilterSelected(List<String> selectedSearchCategories) {
                        eventTypeFilter = new LinkedList<>(CollectionUtils.map(selectedSearchCategories, new CollectionUtils.Mapper<String, EventType>() {
                            @Override
                            public EventType map(String item) {
                                return new EventType(item);
                            }
                        }));

                        applyFiltersInBackground();
                    }
                });

                if (eventTypeFilter.size() > 0) {
                    for (EventType eventType : eventTypeFilter) {
                        searchCategoriesLayout.checkSearchCategory(eventType.getDescription());
                    }
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
		        applyFiltersInBackground();
	        }
        });

        if (keywordsFilter != null) {
            keywordTextBox.setText(keywordsFilter);
        }
    }

	private void applyFiltersInBackground() {
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
			protected void onPostExecute(List<ConventionEvent> events) {
				adapter.setItems(events);
				setNoResultsVisibility(adapter.getCount());
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
        String filteredEventDescription = event.getDescription().isEmpty() ? "" : Html.fromHtml(event.getDescription()).toString();

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
