package amai.org.conventions.events.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.holders.SwipeableEventViewHolder;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;

public class DismissibleEventsViewAdapter extends RecyclerView.Adapter<SwipeableEventViewHolder> {
    private List<OnDatasetChangedListener> onDatasetChangedListeners;
    private List<ConventionEvent> eventsList;
    private final boolean conflicting;

    public DismissibleEventsViewAdapter(List<ConventionEvent> eventsList, boolean conflicting) {
        this.eventsList = eventsList;
        this.conflicting = conflicting;
        this.onDatasetChangedListeners = new LinkedList<>();
    }

    @Override
    public SwipeableEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dismissable_event_view_holder, parent, false);
        return new SwipeableEventViewHolder(view, SwipeableEventViewHolder.SwipeAction.Dismiss);
    }

    @Override
    public void onBindViewHolder(SwipeableEventViewHolder holder, int position) {
        holder.setModel(eventsList.get(position), conflicting);

        EventSwipeListener listener = new EventSwipeListener(position, this);
        holder.addOnSwipeListener(listener);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public void addOnDatasetChangedListener(OnDatasetChangedListener onDatasetChangedListener) {
        onDatasetChangedListeners.add(onDatasetChangedListener);
    }

    public interface OnDatasetChangedListener {
        void onItemRemoved(int position);
    }

    private class EventSwipeListener extends SimpleSwipeListener {

        private int position;

        public EventSwipeListener(int position, DismissibleEventsViewAdapter adapter) {
            this.position = position;
            adapter.addOnDatasetChangedListener(new OnDatasetChangedListener() {
                @Override
                public void onItemRemoved(int position) {
                    if (position < EventSwipeListener.this.position) {
                        EventSwipeListener.this.position--;
                    }
                }
            });
        }

        @Override
        public void onOpen(SwipeLayout layout) {
            super.onOpen(layout);

            eventsList.get(position).setAttending(false);
            Convention.getInstance().save();

            eventsList.remove(position);
            notifyItemRemoved(position);

            for (OnDatasetChangedListener listener : onDatasetChangedListeners) {
                listener.onItemRemoved(position);
            }
        }
    }
}
