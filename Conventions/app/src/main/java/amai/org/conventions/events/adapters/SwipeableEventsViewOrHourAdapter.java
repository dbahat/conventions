package amai.org.conventions.events.adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.ArraySwipeAdapter;

import java.util.Calendar;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.ProgrammeConventionEvent;
import amai.org.conventions.events.SwipeableEventViewHolder;
import amai.org.conventions.events.holders.EventTimeViewHolder;
import amai.org.conventions.events.holders.EventsViewHolder;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class SwipeableEventsViewOrHourAdapter extends ArraySwipeAdapter implements StickyListHeadersAdapter {

    private List<ProgrammeConventionEvent> events;

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
        SwipeableEventViewHolder holder;
        if (convertView == null) {
            View eventView = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipeable_event_view_holder, parent, false);
            holder = new SwipeableEventViewHolder(eventView);
            convertView = eventView;
            convertView.setTag(holder);
        } else {
            holder = (SwipeableEventViewHolder) convertView.getTag();
            holder.reset();
        }

        holder.setModel(events.get(position).getEvent());

        // Register to swipe open events to add/remove the item from favorites
        holder.addOnSwipeListener(new SimpleSwipeListener() {

            @Override
            public void onOpen(final SwipeLayout layout) {
                super.onOpen(layout);

                // Update the favorite state in the model
                boolean isAttending = events.get(position).getEvent().isAttending();
                events.get(position).getEvent().setAttending(!isAttending);

                // Reset the layout state
                layout.close(false, true);

                // Notify the list view to redraw the UI so the new favorite icon state will apply.
                // Done using handler.post since otherwise the UI shows flickering.
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                       notifyDataSetChanged();
                    }
                });
            }
        });
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        EventTimeViewHolder holder = null;
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
