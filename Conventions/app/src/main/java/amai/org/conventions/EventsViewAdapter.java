package amai.org.conventions;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import amai.org.conventions.model.ConventionEvent;

public class EventsViewAdapter extends RecyclerView.Adapter<EventsViewHolder> {
    private List<ConventionEvent> eventsList;
    private boolean showFavoriteIcon;
    private boolean showHallName;

    public EventsViewAdapter(List<ConventionEvent> eventsList, boolean showFavoriteIcon, boolean showHallName) {
        this.eventsList = eventsList;
        this.showFavoriteIcon = showFavoriteIcon;
        this.showHallName = showHallName;
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
        eventsViewHolder.setModel(eventsList.get(position));
    }
}
