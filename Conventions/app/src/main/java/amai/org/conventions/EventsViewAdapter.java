package amai.org.conventions;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import amai.org.conventions.model.ConventionEvent;

public class EventsViewAdapter extends RecyclerView.Adapter<EventsViewHolder> {
    private ArrayList<ConventionEvent> eventsList;

    public EventsViewAdapter(ArrayList<ConventionEvent> eventsList) {
        this.eventsList = eventsList;
    }

    @Override
    public EventsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.convention_event_view_holder, viewGroup, false);
        return new EventsViewHolder(view, R.id.eventElement);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    @Override
    public void onBindViewHolder(EventsViewHolder eventsViewHolder, int i) {
        eventsViewHolder.setModel(eventsList.get(i));
    }

}
