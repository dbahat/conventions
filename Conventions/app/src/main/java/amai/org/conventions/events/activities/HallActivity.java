package amai.org.conventions.events.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import amai.org.conventions.R;
import amai.org.conventions.events.adapters.SwipeableEventsViewAdapter;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.navigation.NavigationActivity;


public class HallActivity extends NavigationActivity {
    public static final String EXTRA_HALL_NAME = "ExtraHallName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String hallName = getIntent().getStringExtra(EXTRA_HALL_NAME);

        setContentInContentContainer(R.layout.activity_hall);
        setToolbarTitle(hallName);

        RecyclerView hallEventsList = (RecyclerView) findViewById(R.id.hallEventsList);
        ArrayList<ConventionEvent> events = Convention.getInstance().findEventsByHall(hallName);
        Collections.sort(events, new ConventionEventComparator());

        hallEventsList.setAdapter(new SwipeableEventsViewAdapter(events));
        hallEventsList.setLayoutManager(new LinearLayoutManager(this));
    }
}
