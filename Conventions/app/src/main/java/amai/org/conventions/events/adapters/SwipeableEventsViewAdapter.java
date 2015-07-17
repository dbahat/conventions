package amai.org.conventions.events.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.holders.SwipeableEventViewHolder;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;

public class SwipeableEventsViewAdapter extends RecyclerView.Adapter<SwipeableEventViewHolder> {
    private List<ConventionEvent> eventsList;

    public SwipeableEventsViewAdapter(List<ConventionEvent> eventsList) {
        this.eventsList = eventsList;
    }

    @Override
    public SwipeableEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipeable_event_view, parent, false);
        return new SwipeableEventViewHolder(view, false);
    }

    @Override
    public void onBindViewHolder(SwipeableEventViewHolder holder, int position) {
        holder.reset();
        final ConventionEvent event = eventsList.get(position);
        holder.setModel(event, false);

        holder.setOnViewSwipedAction(new Runnable() {
            @Override
            public void run() {
                // Update the favorite state in the model
                final boolean isAttending = event.isAttending();
                event.setAttending(!isAttending);

                // Save the changes
                Convention.getInstance().getStorage().saveUserInput();

                // Notify the list view to redraw the UI so the new favorite icon state will apply
                // for all views of this event
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}
