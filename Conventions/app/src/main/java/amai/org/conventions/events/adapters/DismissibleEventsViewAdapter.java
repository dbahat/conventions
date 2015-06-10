package amai.org.conventions.events.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.holders.DismissibleEventViewHolder;
import amai.org.conventions.model.ConventionEvent;

public class DismissibleEventsViewAdapter extends RecyclerView.Adapter<DismissibleEventViewHolder> implements EventSwipeToDismissListener.ChangingDatasetAdapter {
    private List<EventSwipeToDismissListener.OnDatasetChangedListener> onDatasetChangedListeners;
    private List<ConventionEvent> eventsList;
    private final boolean conflicting;

    public DismissibleEventsViewAdapter(List<ConventionEvent> eventsList, boolean conflicting) {
        this.eventsList = eventsList;
        this.conflicting = conflicting;
        this.onDatasetChangedListeners = new LinkedList<>();
    }

    @Override
    public DismissibleEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dismissable_event_view_holder, parent, false);
        return new DismissibleEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DismissibleEventViewHolder holder, int position) {
	    ConventionEvent event = eventsList.get(position);
	    holder.setModel(event, conflicting);

        EventSwipeToDismissListener listener = new EventSwipeToDismissListener(position, event, eventsList, this);
        holder.addOnSwipeListener(listener);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

	@Override
    public void addOnDatasetChangedListener(EventSwipeToDismissListener.OnDatasetChangedListener onDatasetChangedListener) {
        onDatasetChangedListeners.add(onDatasetChangedListener);
    }

	@Override
	public List<EventSwipeToDismissListener.OnDatasetChangedListener> getOnDatasetChangedListeners() {
		return onDatasetChangedListeners;
	}

	@Override
	public void onViewRecycled(DismissibleEventViewHolder holder) {
		super.onViewRecycled(holder);
		holder.reset();
	}
}
