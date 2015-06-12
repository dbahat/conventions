package amai.org.conventions.events.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import amai.org.conventions.R;
import amai.org.conventions.events.holders.ConflictingEventsViewHolder;
import amai.org.conventions.events.holders.DismissibleEventViewHolder;
import amai.org.conventions.events.holders.SwipeableEventViewHolder;
import amai.org.conventions.model.ConventionEvent;

public class ConflictingEventsViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final int ITEM_VIEW_TYPE_REGULAR = 1;
	private static final int ITEM_VIEW_TYPE_CONFLICTING = 2;

	private ArrayList<ArrayList<ConventionEvent>> eventGroups;

    public ConflictingEventsViewAdapter(ArrayList<ArrayList<ConventionEvent>> eventGroups) {
        this.eventGroups = eventGroups;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
	    switch (viewType) {
		    case ITEM_VIEW_TYPE_REGULAR : {
			    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dismissable_event_view_holder, viewGroup, false);
			    return new DismissibleEventViewHolder(view);
		    }
		    case ITEM_VIEW_TYPE_CONFLICTING : {
		        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.conflicting_events_view_holder, viewGroup, false);
		        return new ConflictingEventsViewHolder(view, viewGroup.getContext());
		    }
	    }
	    throw new RuntimeException("Unexpected view type " + viewType);
    }

    @Override
    public int getItemCount() {
        return eventGroups.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder eventsViewHolder, final int i) {
	    if (eventsViewHolder instanceof DismissibleEventViewHolder) {
		    ((DismissibleEventViewHolder) eventsViewHolder).reset();
	    } else if (eventsViewHolder instanceof SwipeableEventViewHolder) {
		    ((SwipeableEventViewHolder) eventsViewHolder).reset();
	    }

	    if (eventsViewHolder instanceof DismissibleEventViewHolder) {
			final DismissibleEventViewHolder swipeableEventViewHolder = (DismissibleEventViewHolder) eventsViewHolder;
		    ConventionEvent event = eventGroups.get(i).get(0);
		    swipeableEventViewHolder.setModel(event);

			EventSwipeToDismissListener listener = new EventSwipeToDismissListener((DismissibleEventViewHolder) eventsViewHolder, eventGroups, this);
			swipeableEventViewHolder.addOnSwipeListener(listener);

	    } else if (eventsViewHolder instanceof ConflictingEventsViewHolder) {
		    ((ConflictingEventsViewHolder) eventsViewHolder).setModel(eventGroups.get(i));
	    }
    }

	@Override
	public int getItemViewType(int position) {
		return eventGroups.get(position).size() > 1 ? ITEM_VIEW_TYPE_CONFLICTING : ITEM_VIEW_TYPE_REGULAR;
	}
}
