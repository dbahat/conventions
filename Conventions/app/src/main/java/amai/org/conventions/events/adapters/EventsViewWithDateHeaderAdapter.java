package amai.org.conventions.events.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.holders.EventViewHolder;
import amai.org.conventions.events.holders.TimeViewHolder;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.utils.Dates;

public class EventsViewWithDateHeaderAdapter extends BaseAdapter {
	private static final int ITEM_TYPE_EVENT = 0;
	private static final int ITEM_TYPE_DATE = 1;
	private List<Date> datesList;
	private List<Object> eventsAndDates;

	public EventsViewWithDateHeaderAdapter(List<ConventionEvent> eventsList) {
		calculateItems(eventsList);
	}

	public void setEventsList(List<ConventionEvent> eventsList) {
		calculateItems(eventsList);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return eventsAndDates.size();
	}

	@Override
	public Object getItem(int position) {
		return eventsAndDates.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Object eventOrDate = eventsAndDates.get(position);

		if (getItemViewType(position) == ITEM_TYPE_EVENT) {
			final EventViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.convention_event_view_holder, parent, false);
				holder = new EventViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (EventViewHolder) convertView.getTag();
			}
			holder.setModel((ConventionEvent) eventOrDate);
		} else {
			final TimeViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.small_text_view, parent, false);
				holder = new TimeViewHolder(convertView, R.id.small_text);
				convertView.setTag(holder);
			} else {
				holder = (TimeViewHolder) convertView.getTag();
			}
			holder.setTime((Date) eventOrDate, "EEE (dd.MM)");
		}

		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		return eventsAndDates.get(position) instanceof ConventionEvent ? ITEM_TYPE_EVENT : ITEM_TYPE_DATE;
	}

	@Override
	public int getViewTypeCount() {
		return 2; // event and date
	}

	private void calculateItems(List<ConventionEvent> eventList) {
		// TODO (david): For single day convention, hide the header. After Convention object is merged.
		eventsAndDates = new ArrayList<>(eventList.size() + 1);
		Date currHeader = null;
		for (ConventionEvent event : eventList) {
			Date header = getHeader(event);
			if (currHeader == null || !currHeader.equals(header)) {
				eventsAndDates.add(header);
			}
			eventsAndDates.add(event);
			currHeader = header;
		}
	}

	private Date getHeader(ConventionEvent event) {
		Calendar calendar = Dates.toCalendar(event.getStartTime());
		return Dates.createDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).getTime();
	}
}

