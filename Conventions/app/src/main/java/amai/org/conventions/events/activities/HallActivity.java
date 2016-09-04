package amai.org.conventions.events.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import amai.org.conventions.R;
import amai.org.conventions.events.adapters.SwipeableEventsViewAdapter;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.navigation.NavigationActivity;


public class HallActivity extends NavigationActivity {
    public static final String EXTRA_HALL_NAME = "ExtraHallName";
	public static final String EXTRA_USE_SLIDE_OUT_ANIMATION_ON_BACK = "ExtraUseSlideOutAnimationOnBack";
	private boolean useSlideOutAnimationOnBack;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String hallName = getIntent().getStringExtra(EXTRA_HALL_NAME);
	    useSlideOutAnimationOnBack = getIntent().getBooleanExtra(EXTRA_USE_SLIDE_OUT_ANIMATION_ON_BACK, false);

        setContentInContentContainer(R.layout.activity_hall);
        setToolbarTitle(hallName);

        RecyclerView hallEventsList = (RecyclerView) findViewById(R.id.hallEventsList);
        ArrayList<ConventionEvent> events = Convention.getInstance().findEventsByHall(hallName);
        Collections.sort(events, new ConventionEventComparator());

        hallEventsList.setAdapter(new SwipeableEventsViewAdapter(events, hallEventsList));
        hallEventsList.setLayoutManager(new LinearLayoutManager(this));
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (useSlideOutAnimationOnBack) {
			overridePendingTransition(0, R.anim.slide_out_bottom);
		}
	}
}
