package amai.org.conventions.events.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.holders.DismissibleEventViewHolder;
import amai.org.conventions.model.ConventionEvent;

public class DismissibleEventsViewAdapter extends RecyclerView.Adapter<DismissibleEventViewHolder> {
    private List<ConventionEvent> eventsList;
    private final boolean conflicting;

    public DismissibleEventsViewAdapter(List<ConventionEvent> eventsList, boolean conflicting) {
        this.eventsList = eventsList;
        this.conflicting = conflicting;
    }

    @Override
    public DismissibleEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dismissible_event_view, parent, false);
        return new DismissibleEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DismissibleEventViewHolder holder, int position) {
	    holder.reset();
	    holder.setModel(eventsList.get(position), conflicting);

        EventSwipeToDismissListener listener = new EventSwipeToDismissListener(holder, eventsList, this);
        holder.addOnSwipeListener(listener);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}
