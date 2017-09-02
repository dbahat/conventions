package amai.org.conventions.events.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Calendar;
import java.util.List;

import sff.org.conventions.R;
import amai.org.conventions.events.DefaultEventFavoriteChangedListener;
import amai.org.conventions.events.holders.EventViewHolder;
import amai.org.conventions.events.holders.TimeViewHolder;
import amai.org.conventions.events.listeners.OnEventFavoriteChangedListener;
import amai.org.conventions.model.ConventionEvent;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class EventsViewListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

	private List<ConventionEvent> events;
	private final boolean showHeaders;
	private List<String> keywordsToHighlight;
	private OnEventFavoriteChangedListener listener;

	public EventsViewListAdapter(List<ConventionEvent> events, View listView, boolean showHeaders) {
		this.events = events;
		this.showHeaders = showHeaders;
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
		final EventViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.convention_event_view_holder, parent, false);
			holder = new EventViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (EventViewHolder) convertView.getTag();
		}

		final ConventionEvent event = events.get(position);
		holder.setModel(event);

		boolean isLastInNonLastSection = false;
		if (position < events.size() - 1 && getHeaderId(position) != getHeaderId(position + 1)) {
			isLastInNonLastSection = true;
		}
		holder.setBottomDividerVisible(isLastInNonLastSection);

		if (keywordsToHighlight != null) {
			holder.getEventView().setKeywordsHighlighting(keywordsToHighlight);
		}

		holder.getEventView().setOnFavoritesButtonClickedListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onEventFavoriteChanged(event);
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
		// Hide the header view if we don't want to show headers.
		// This is the only possible way to do this except using a different list view which is not sticky.
		// Setting the view visibility to GONE or giving it height = 0 doesn't work, and returning a null view
		// throws NPE.
		if (!showHeaders) {
			convertView.setVisibility(View.INVISIBLE);
			convertView.getLayoutParams().height = 1;
			convertView.setLayoutParams(convertView.getLayoutParams());
		}
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
