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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.SearchCategoriesLayout;
import amai.org.conventions.events.adapters.SwipeableEventsViewAdapter;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.EventType;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Views;

public class ProgrammeSearchActivity extends NavigationActivity {

    private static final String STATE_KEYWORDS_FILTER = "KeywordsFilter";
    private static final String STATE_EVENT_TYPES_FILTER = "EventTypesFilter";

    private LinkedList<EventType> eventTypeFilter;
    private String keywordsFilter;
    private SwipeableEventsViewAdapter adapter;
    private RecyclerView recyclerView;
    private TextView noResultsFoundView;

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
        if (item.getItemId() == R.id.programme_search_back) {
            supportFinishAfterTransition();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeEventsList() {
        recyclerView = (RecyclerView) findViewById(R.id.searchEventsList);

        adapter = new SwipeableEventsViewAdapter(Collections.<ConventionEvent>emptyList(), recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initializeSearchCategories() {
        final SearchCategoriesLayout searchCategoriesLayout = (SearchCategoriesLayout) findViewById(R.id.search_categories_layout);
        searchCategoriesLayout.setOnFilterSelectedListener(new SearchCategoriesLayout.OnFilterSelectedListener() {
	        @Override
	        public void onFilterSelected(final List<EventType> selectedEventTypes) {
		        eventTypeFilter = new LinkedList<>(selectedEventTypes);
		        applyFiltersInBackground();
	        }
        });

        if (eventTypeFilter != null) {
            for (EventType eventType : eventTypeFilter) {
                searchCategoriesLayout.toggleEventType(eventType);
            }
        }
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

		new AsyncTask<Void, Void, List<ConventionEvent>>() {
			@Override
			protected List<ConventionEvent> doInBackground(Void... params) {
				return filterEvents(keywordsFilter, eventTypeFilter);
			}

			@Override
			protected void onPostExecute(List<ConventionEvent> events) {
				adapter.setEventsList(events);
				setNoResultsVisibility(adapter.getItemCount());
			}
		}.execute();
	}

    private List<ConventionEvent> filterEvents(final String keywordsFilter, final List<EventType> eventTypeFilter) {
        List<ConventionEvent> events = Convention.getInstance().getEvents();

        events = CollectionUtils.filter(events, new CollectionUtils.Predicate<ConventionEvent>() {
            @Override
            public boolean where(ConventionEvent item) {
                boolean result = true;
	            if (keywordsFilter != null && keywordsFilter.length() > 0) {
                    result = containsKeywords(item);
                }
                if (eventTypeFilter != null && eventTypeFilter.size() > 0) {
                    result &= eventTypeFilter.contains(item.getType());
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
		    recyclerView.setVisibility(View.GONE);
		} else {
		    noResultsFoundView.setVisibility(View.GONE);
		    recyclerView.setVisibility(View.VISIBLE);
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
}
