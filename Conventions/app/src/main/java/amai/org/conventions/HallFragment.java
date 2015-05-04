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

import amai.org.conventions.R;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;

/**
 * A simple {@link Fragment} subclass.
 */
public class HallFragment extends Fragment {
    private static final String hallName = "אורנים 2";

    public HallFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hall, container, false);

        RecyclerView hallEventsList = (RecyclerView) view.findViewById(R.id.hallEventsList);
        List<ConventionEvent> fullEventsList = Convention.getInstance().getEvents();
        hallEventsList.setAdapter(new EventsViewAdapter(filter(fullEventsList, this.hallName)));
        hallEventsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    private static ArrayList<ConventionEvent> filter(List<ConventionEvent> fullEventsList, String hallName) {
        ArrayList<ConventionEvent> result = new ArrayList<>();
        for (ConventionEvent event: fullEventsList) {
            if (hallName.equals(event.getHall().getName())) {
                result.add(event);
            }
        }
        return result;
    }
}
