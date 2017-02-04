package amai.org.conventions.events.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Calendar;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.DefaultEventFavoriteChangedListener;
import amai.org.conventions.events.holders.TimeViewHolder;
import amai.org.conventions.events.holders.SwipeableEventViewHolder;
import amai.org.conventions.events.listeners.OnEventFavoriteChangedListener;
import amai.org.conventions.model.ConventionEvent;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class SwipeableEventsViewListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

	private List<ConventionEvent> events;
	private List<String> keywordsToHighlight;
	private OnEventFavoriteChangedListener listener;

    public SwipeableEventsViewListAdapter(List<ConventionEvent> events, View listView) {
        this.events = events;
	    this.listener = new DefaultEventFavoriteChangedListener(listView);
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public ConventionEvent getItem(int position) {
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
	        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipeable_event_view, parent, false);
            holder = new SwipeableEventViewHolder(convertView, false);
            convertView.setTag(holder);
        } else {
            holder = (SwipeableEventViewHolder) convertView.getTag();
        }

	    final ConventionEvent event = events.get(position);
	    holder.setModel(event);

	    if (keywordsToHighlight != null) {
		    holder.setKeywordsHighlighting(keywordsToHighlight);
	    }

        // Register to swipe open events to add/remove the item from favorites
        holder.setOnViewSwipedAction(new Runnable() {
	        @Override
	        public void run() {
		        if (listener != null) {
			        listener.onEventFavoriteChanged(event);
		        }
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
        TimeViewHolder holder;
        if (convertView == null) {
            View eventTimeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.convention_event_time, parent, false);
            holder = new TimeViewHolder(eventTimeView, R.id.event_time_text_view);
            convertView = eventTimeView;
            convertView.setTag(holder);
        } else {
            holder = (TimeViewHolder) convertView.getTag();
        }

	    ConventionEvent event = events.get(position);
	    holder.setTime(event.getStartTime(), "EEE (dd.MM)");
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
	    ConventionEvent event = events.get(position);
	    Calendar eventStartTime = Calendar.getInstance();
	    eventStartTime.setTime(event.getStartTime());
	    return eventStartTime.get(Calendar.DAY_OF_MONTH);
    }

    public void setItems(List<ConventionEvent> events) {
        this.events = events;
        notifyDataSetChanged();
    }

	public void setKeywordsHighlighting(List<String> keywords) {
		keywordsToHighlight = keywords;
	}
}
