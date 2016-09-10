package amai.org.conventions.events.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import sff.org.conventions.R;
import amai.org.conventions.events.holders.EventViewHolder;
import amai.org.conventions.model.ConventionEvent;

public class EventsViewAdapter extends BaseAdapter {
	private List<ConventionEvent> eventsList;

	public EventsViewAdapter(List<ConventionEvent> eventsList) {
		this.eventsList = eventsList;
	}

	public void setEventsList(List<ConventionEvent> eventsList) {
		this.eventsList = eventsList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return eventsList.size();
	}

	@Override
	public Object getItem(int position) {
		return eventsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final EventViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.convention_event_view_holder, parent, false);
			holder = new EventViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (EventViewHolder) convertView.getTag();
		}

		holder.setModel(eventsList.get(position));
		return convertView;
	}
}

