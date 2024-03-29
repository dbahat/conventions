package amai.org.conventions.events.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.ProgrammeConventionEvent;
import amai.org.conventions.events.holders.EventViewHolder;
import amai.org.conventions.events.holders.TimeViewHolder;
import amai.org.conventions.events.listeners.OnEventFavoriteChangedListener;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.networking.AmaiModelConverter;
import amai.org.conventions.utils.Dates;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class EventsViewOrHourAdapter extends BaseAdapter implements StickyListHeadersAdapter {

	private List<ProgrammeConventionEvent> events;
	private OnEventFavoriteChangedListener eventFavoriteChangedListener;

	public EventsViewOrHourAdapter(List<ProgrammeConventionEvent> events) {
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
		final EventViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.convention_event_view_holder, parent, false);
			holder = new EventViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (EventViewHolder) convertView.getTag();
		}

		final ConventionEvent event = events.get(position).getEvent();
		holder.setModel(event);
		holder.getEventView().setOnFavoritesButtonClickedListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				eventFavoriteChangedListener.onEventFavoriteChanged(holder.getEvent());
			}
		});

		boolean isLastInNonLastSection = false;
		if (position < events.size() - 1 && getHeaderId(position) != getHeaderId(position + 1)) {
			isLastInNonLastSection = true;
		}
		holder.setBottomDividerVisible(isLastInNonLastSection);

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

		Date time = events.get(position).getTimeSection().getTime();
		holder.setTime(time, "HH:00");
		holder.setTextColor(getTimeColor(holder, time));
		return convertView;
	}

	private int getTimeColor(TimeViewHolder holder, Date startTime) {
		Calendar endTimeCalendar = Calendar.getInstance();
		endTimeCalendar.setTimeInMillis(startTime.getTime() + Dates.MILLISECONDS_IN_HOUR);
		Date endTime = endTimeCalendar.getTime();

		int[] attributes = new int[1];
		if (!startTime.before(Dates.now())) {
			attributes[0] = R.attr.state_event_not_started;
		} else if (endTime.before(Dates.now())) {
			attributes[0] = R.attr.state_event_ended;
		} else {
			attributes[0] = R.attr.state_event_current;
		}
		return ThemeAttributes.getColorFromStateList(holder.itemView.getContext(), R.attr.eventTimeHeaderTextColor, attributes);
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
