package amai.org.conventions.events.adapters;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.R;
import amai.org.conventions.events.holders.SwipeableEventViewHolder;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;

public class SwipeableEventsViewAdapter extends RecyclerView.Adapter<SwipeableEventViewHolder> {
    private List<ConventionEvent> eventsList;
	private View recyclerView;
    private List<String> keywordsToHighlight;

	public SwipeableEventsViewAdapter(List<ConventionEvent> eventsList, View recyclerView) {
        this.eventsList = eventsList;
		this.recyclerView = recyclerView;
	}

    @Override
    public SwipeableEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipeable_event_view, parent, false);
        return new SwipeableEventViewHolder(view, false);
    }

    @Override
    public void onBindViewHolder(final SwipeableEventViewHolder holder, int position) {
        holder.reset();
        final ConventionEvent event = eventsList.get(position);
        holder.setModel(event, false);

        if (keywordsToHighlight != null) {
            holder.setKeywordsHighlighting(keywordsToHighlight);
        }

        holder.setOnViewSwipedAction(new Runnable() {
            @Override
            public void run() {
                // Update the favorite state in the model
                final boolean isAttending = event.isAttending();
                event.setAttending(!isAttending);

                if (isAttending) {
                    event.setAttending(false);
                    ConventionsApplication.alarmScheduler.cancelDefaultEventAlarms(event);
                } else {
                    event.setAttending(true);
                    ConventionsApplication.alarmScheduler.scheduleDefaultEventAlarms(event);
                }

                // Save the changes
                Convention.getInstance().getStorage().saveUserInput();

                // Notify the list view to redraw the UI so the new favorite icon state will apply
                // for all views of this event
                notifyDataSetChanged();

	            // isAttending contains the previous value of the attending flag
	            if (isAttending) {
		            Snackbar.make(recyclerView, R.string.event_removed_from_favorites, Snackbar.LENGTH_SHORT).show();
	            } else {
		            Snackbar.make(recyclerView, R.string.event_added_to_favorites, Snackbar.LENGTH_SHORT).show();
	            }
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public void setEventsList(List<ConventionEvent> eventsList) {
        this.eventsList = eventsList;
        notifyDataSetChanged();
    }

    public void setKeywordsHighlighting(List<String> keywords) {
        keywordsToHighlight = keywords;
    }
}
