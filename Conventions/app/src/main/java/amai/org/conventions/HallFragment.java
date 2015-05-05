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
import java.util.List;

import amai.org.conventions.model.CollectionsFilter;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class HallFragment extends Fragment {
    private final String hallName = "אורנים 2";

    public HallFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hall, container, false);

        RecyclerView hallEventsList = (RecyclerView) view.findViewById(R.id.hallEventsList);
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

        hallEventsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
}
