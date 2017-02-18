package amai.org.conventions.events.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Calendar;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.ProgrammeConventionEvent;
import amai.org.conventions.events.holders.SwipeableEventViewHolder;
import amai.org.conventions.events.holders.TimeViewHolder;
import amai.org.conventions.events.listeners.OnEventFavoriteChangedListener;
import amai.org.conventions.model.ConventionEvent;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class SwipeableEventsViewOrHourAdapter extends BaseAdapter implements StickyListHeadersAdapter {

	private List<ProgrammeConventionEvent> events;
	private OnEventFavoriteChangedListener eventFavoriteChangedListener;

	public SwipeableEventsViewOrHourAdapter(List<ProgrammeConventionEvent> events) {
		this.events = events;
	}

	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public ProgrammeConventionEvent getItem(int position) {
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

		final ConventionEvent event = events.get(position).getEvent();
		holder.setModel(event);

		boolean isLastInNonLastSection = false;
		if (position < events.size() - 1 && getHeaderId(position) != getHeaderId(position + 1)) {
			isLastInNonLastSection = true;
		}
		holder.setBottomDividerVisible(isLastInNonLastSection);

		// Register to swipe open events to add/remove the item from favorites
		holder.setOnViewSwipedAction(new Runnable() {
			@Override
			public void run() {
				if (eventFavoriteChangedListener != null) {
					eventFavoriteChangedListener.onEventFavoriteChanged(event);
				}
			}
		});

		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	public void setOnEventFavoriteChangedListener(OnEventFavoriteChangedListener action) {
		this.eventFavoriteChangedListener = action;
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

		holder.setTime(events.get(position).getTimeSection().getTime(), "HH:00");
		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		return events.get(position).getTimeSection().get(Calendar.HOUR_OF_DAY);
	}

	public void setItems(List<ProgrammeConventionEvent> events) {
		this.events = events;
		notifyDataSetChanged();
	}
}
