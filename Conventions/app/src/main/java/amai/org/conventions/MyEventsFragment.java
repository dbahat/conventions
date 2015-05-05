package amai.org.conventions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;


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
        List<ConventionEvent> fullEventsList = Convention.getInstance().getEvents();
        hallEventsList.setAdapter(new EventsViewAdapter(filter(fullEventsList)));
        hallEventsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    private static ArrayList<ConventionEvent> filter(List<ConventionEvent> fullEventsList) {
        ArrayList<ConventionEvent> result = new ArrayList<>();
        for (ConventionEvent event: fullEventsList) {
            if (event.isAttending()) {
                result.add(event);
            }
        }
        return result;
    }
}
