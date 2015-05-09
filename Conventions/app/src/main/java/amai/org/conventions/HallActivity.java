package amai.org.conventions;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import amai.org.conventions.model.CollectionsFilter;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.navigation.NavigationToolbar;


public class HallActivity extends NavigationActivity {
    private final String hallName = "אורנים 2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_hall);
        setToolbarTitle(hallName);

        RecyclerView hallEventsList = (RecyclerView) findViewById(R.id.hallEventsList);
        final String hallName = this.hallName;
        ArrayList<ConventionEvent> events = CollectionsFilter.filter(
                Convention.getInstance().getEvents(),
                new CollectionsFilter.Predicate<ConventionEvent>() {
                    @Override
                    public boolean where(ConventionEvent event) {
                        return hallName.equals(event.getHall().getName());
                    }
                },
                new ArrayList<ConventionEvent>()
        );
        Collections.sort(events, new ConventionEventComparator());
        hallEventsList.setAdapter(new EventsViewAdapter(events, true, false));

        hallEventsList.setLayoutManager(new LinearLayoutManager(this));
    }
}
