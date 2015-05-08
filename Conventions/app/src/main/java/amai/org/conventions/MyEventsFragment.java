package amai.org.conventions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

import amai.org.conventions.model.CollectionsFilter;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.navigation.NavigationActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyEventsFragment extends Fragment {
    public MyEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        RecyclerView hallEventsList = (RecyclerView) view.findViewById(R.id.myEventsList);
        ArrayList<ConventionEvent> events = CollectionsFilter.filter(
                Convention.getInstance().getEvents(),
                new CollectionsFilter.Predicate<ConventionEvent>() {
                    @Override
                    public boolean where(ConventionEvent event) {
                        return event.isAttending();
                    }
                },
                new ArrayList<ConventionEvent>()
        );
        Collections.sort(events, new ConventionEventComparator());
        hallEventsList.setAdapter(new EventsViewAdapter(events, true, true));

        hallEventsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            NavigationActivity navigationActivity = (NavigationActivity) getActivity();
            navigationActivity.setTitle(getResources().getString(R.string.my_events_title));
        }
    }
}
