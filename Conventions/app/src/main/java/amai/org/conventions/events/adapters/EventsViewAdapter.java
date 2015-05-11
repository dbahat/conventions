package amai.org.conventions.events.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.holders.EventsViewHolder;
import amai.org.conventions.model.ConventionEvent;

public class EventsViewAdapter extends RecyclerView.Adapter<EventsViewHolder> {
    private List<ConventionEvent> eventsList;
    private boolean showFavoriteIcon;
    private boolean showHallName;
	private final boolean conflicting;

    public EventsViewAdapter(List<ConventionEvent> eventsList, boolean showFavoriteIcon, boolean showHallName, boolean conflicting) {
        this.eventsList = eventsList;
        this.showFavoriteIcon = showFavoriteIcon;
        this.showHallName = showHallName;
	    this.conflicting = conflicting;
    }

    @Override
    public EventsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.convention_event_view_holder, viewGroup, false);
        return new EventsViewHolder(view, R.id.eventElement, showFavoriteIcon, showHallName);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    @Override
    public void onBindViewHolder(EventsViewHolder eventsViewHolder, int position) {
        eventsViewHolder.setModel(eventsList.get(position), conflicting);
    }
}

