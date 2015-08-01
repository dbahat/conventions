package amai.org.conventions.events.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.SearchCategoriesLayout;
import amai.org.conventions.events.adapters.SwipeableEventsViewAdapter;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.EventType;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.CollectionUtils;

public class ProgrammeSearchActivity extends NavigationActivity {

    private static final String STATE_KEYWORDS_FILTER = "KeywordsFilter";
    private static final String STATE_EVENT_TYPES_FILTER = "EventTypesFilter";

    private LinkedList<EventType> eventTypeFilter;
    private String keywordsFilter;
    private SwipeableEventsViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_programme_search);
        setToolbarTitle(getResources().getString(R.string.programme_search_title));

        if (savedInstanceState != null) {
            eventTypeFilter = (LinkedList<EventType>) savedInstanceState.getSerializable(STATE_EVENT_TYPES_FILTER);
            keywordsFilter = savedInstanceState.getString(STATE_KEYWORDS_FILTER);
        }

        initializeEventsList();
        initializeKeywordFilter();
        initializeSearchCategories();

        applyFilters();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(STATE_KEYWORDS_FILTER, keywordsFilter);
        outState.putSerializable(STATE_EVENT_TYPES_FILTER, eventTypeFilter);
    }

    private void initializeEventsList() {
        RecyclerView searchEventsList = (RecyclerView) findViewById(R.id.searchEventsList);

        adapter = new SwipeableEventsViewAdapter(null, searchEventsList);
        searchEventsList.setAdapter(adapter);
        searchEventsList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initializeSearchCategories() {
        SearchCategoriesLayout searchCategoriesLayout = (SearchCategoriesLayout) findViewById(R.id.search_categories_layout);
        searchCategoriesLayout.setOnFilterSelectedListener(new SearchCategoriesLayout.OnFilterSelectedListener() {
            @Override
            public void onFilterSelected(final List<EventType> selectedEventTypes) {
                eventTypeFilter = new LinkedList<>(selectedEventTypes);
                applyFilters();
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
            public void afterTextChanged(final Editable s) {
                keywordsFilter = s.toString();
                applyFilters();
            }
        });

        if (keywordsFilter != null) {
            keywordTextBox.setText(keywordsFilter);
        }
    }

    private void applyFilters() {
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
        adapter.setEventsList(events);
    }

    private boolean containsKeywords(ConventionEvent event) {
        boolean result = false;

        // Split the keyword string into words, and search each word with logical OR
        for (String keyword : keywordsFilter.split(" ")) {
            result |= containsKeyword(event, keyword);
        }

        return result;
    }

    private boolean containsKeyword(ConventionEvent event, String keyword) {
        keyword = keyword.toLowerCase();
        return event.getTitle().toLowerCase().contains(keyword)
                || event.getLecturer().contains(keyword)
                || event.getHall().getName().contains(keyword);
    }
}
