package amai.org.conventions.events.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.activities.MyEventsActivity;
import amai.org.conventions.events.holders.ConflictingEventsViewHolder;
import amai.org.conventions.events.holders.SwipeableEventViewHolder;
import amai.org.conventions.events.listeners.EventSwipeToDismissListener;
import amai.org.conventions.model.ConventionEvent;

public class EventGroupsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final int ITEM_VIEW_TYPE_REGULAR = 1;
	private static final int ITEM_VIEW_TYPE_CONFLICTING = 2;

	private ArrayList<ArrayList<ConventionEvent>> eventGroups;
	private Runnable onEventRemovedAction;

    public EventGroupsAdapter(ArrayList<ArrayList<ConventionEvent>> eventGroups) {
        this.eventGroups = eventGroups;

	    registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
		    @Override
		    public void onItemRangeRemoved(int positionStart, int itemCount) {
			    super.onItemRangeRemoved(positionStart, itemCount);
			    if (onEventRemovedAction != null) {
			        onEventRemovedAction.run();
			    }
		    }
	    });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
	    switch (viewType) {
		    case ITEM_VIEW_TYPE_REGULAR : {
			    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.swipeable_event_view, viewGroup, false);
			    return new SwipeableEventViewHolder(view, true);
		    }
		    case ITEM_VIEW_TYPE_CONFLICTING : {
		        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.conflicting_events_view, viewGroup, false);
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
    public void onBindViewHolder(RecyclerView.ViewHolder eventsViewHolder, final int position) {
	    if (eventsViewHolder instanceof SwipeableEventViewHolder) {
			final SwipeableEventViewHolder dismissibleEventViewHolder = (SwipeableEventViewHolder) eventsViewHolder;
		    dismissibleEventViewHolder.setModel(eventGroups.get(position).get(0));

			EventSwipeToDismissListener listener = new EventSwipeToDismissListener(dismissibleEventViewHolder, eventGroups, this);
			dismissibleEventViewHolder.setOnViewSwipedAction(listener);

	    } else if (eventsViewHolder instanceof ConflictingEventsViewHolder) {
		    final ConflictingEventsViewHolder conflictingEventsViewHolder = (ConflictingEventsViewHolder) eventsViewHolder;
		    conflictingEventsViewHolder.setModel(eventGroups.get(position));
		    conflictingEventsViewHolder.setEventRemovedListener(new Runnable() {
			    @Override
			    public void run() {
				    int adapterPosition = conflictingEventsViewHolder.getAdapterPosition();
				    List<ConventionEvent> eventsList = conflictingEventsViewHolder.getModel();
				    // If after removal only 1 item remains, the item type has changed
				    if (eventsList.size() == 1) {
					    // Use remove and insert instead of changed for fade-out and fade-in animation, to make it seem
					    // like a bigger change
					    notifyItemRemoved(adapterPosition);
					    notifyItemInserted(adapterPosition);
				    } else {
					    ArrayList<ArrayList<ConventionEvent>> groups = MyEventsActivity.getNonConflictingGroups(eventsList);
					    // If the number of groups changed, remove the group and insert the new groups
					    if (groups.size() != 1) {
						    eventGroups.remove(adapterPosition);
						    eventGroups.addAll(adapterPosition, groups);
						    notifyItemRemoved(adapterPosition);
						    notifyItemRangeInserted(adapterPosition, groups.size());
					    }
				    }

				    if (onEventRemovedAction != null) {
					    onEventRemovedAction.run();
				    }
			    }
		    });
	    }
    }

	public void setOnEventRemovedAction(final Runnable onEventRemovedAction) {
		this.onEventRemovedAction = onEventRemovedAction;
	}

	@Override
	public int getItemViewType(int position) {
		return eventGroups.get(position).size() > 1 ? ITEM_VIEW_TYPE_CONFLICTING : ITEM_VIEW_TYPE_REGULAR;
	}

	public ArrayList<ArrayList<ConventionEvent>> getEventGroups() {
		return eventGroups;
	}
}
