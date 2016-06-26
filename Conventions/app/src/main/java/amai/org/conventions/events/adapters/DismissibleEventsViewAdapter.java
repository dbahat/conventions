package amai.org.conventions.events.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.holders.SwipeableEventViewHolder;
import amai.org.conventions.events.listeners.EventSwipeToDismissListener;
import amai.org.conventions.model.ConventionEvent;

public class DismissibleEventsViewAdapter extends RecyclerView.Adapter<SwipeableEventViewHolder> {
    private List<ConventionEvent> eventsList;

    public DismissibleEventsViewAdapter(List<ConventionEvent> eventsList) {
        this.eventsList = eventsList;
    }

    @Override
    public SwipeableEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipeable_event_view, parent, false);
        return new SwipeableEventViewHolder(view, true);
    }

    @Override
    public void onBindViewHolder(SwipeableEventViewHolder holder, int position) {
	    holder.reset();
	    holder.setModel(eventsList.get(position), true);

        EventSwipeToDismissListener listener = new EventSwipeToDismissListener(holder, eventsList, this);
        holder.setOnViewSwipedAction(listener);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

	public List<ConventionEvent> getEventsList() {
		return eventsList;
	}
}
