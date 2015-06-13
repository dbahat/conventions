package amai.org.conventions.events.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.adapters.ArraySwipeAdapter;

import java.util.Calendar;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.ProgrammeConventionEvent;
import amai.org.conventions.events.holders.EventTimeViewHolder;
import amai.org.conventions.events.holders.SwipeableEventViewHolder;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class SwipeableEventsViewOrHourAdapter extends ArraySwipeAdapter<ProgrammeConventionEvent> implements StickyListHeadersAdapter {

    private List<ProgrammeConventionEvent> events;
	private Runnable eventFavoriteChangedListener;

    public SwipeableEventsViewOrHourAdapter(Context context, int resource, List<ProgrammeConventionEvent> events) {
        super(context, resource);
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final SwipeableEventViewHolder holder;
        if (convertView == null) {
	        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipeable_event_view_holder, parent, false);
            holder = new SwipeableEventViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SwipeableEventViewHolder) convertView.getTag();
        }

	    final ConventionEvent event = events.get(position).getEvent();
	    holder.setModel(event);

        // Register to swipe open events to add/remove the item from favorites
        holder.setOnViewSwipedAction(new Runnable() {
	        @Override
	        public void run() {
		        // Update the favorite state in the model
		        final boolean isAttending = event.isAttending();
		        event.setAttending(!isAttending);

		        // Save the changes
		        Convention.getInstance().save();

		        // Notify the list view to redraw the UI so the new favorite icon state will apply
		        // for all views of this event
		        notifyDataSetChanged();

		        if (eventFavoriteChangedListener != null) {
			        eventFavoriteChangedListener.run();
		        }
	        }
        });

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

	public void setOnEventFavoriteChangedListener(Runnable action) {
		this.eventFavoriteChangedListener = action;
	}

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        EventTimeViewHolder holder;
        if (convertView == null) {
            View eventTimeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.convention_event_time, parent, false);
            holder = new EventTimeViewHolder(eventTimeView);
            convertView = eventTimeView;
            convertView.setTag(holder);
        } else {
            holder = (EventTimeViewHolder) convertView.getTag();
        }

        holder.setTime(events.get(position).getTimeSection().getTime());
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return events.get(position).getTimeSection().get(Calendar.HOUR_OF_DAY);
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return 0;
    }
}
