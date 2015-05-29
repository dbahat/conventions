package amai.org.conventions.events.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.ProgrammeConventionEvent;
import amai.org.conventions.events.holders.EventTimeViewHolder;
import amai.org.conventions.events.holders.EventsViewHolder;
import amai.org.conventions.model.ConventionEvent;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class EventsViewOrHourAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private List<ProgrammeConventionEvent> events;

    public EventsViewOrHourAdapter(List<ProgrammeConventionEvent> events) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        EventsViewHolder holder;
        if (convertView == null) {
            View eventView = LayoutInflater.from(parent.getContext()).inflate(R.layout.convention_event_view_holder, parent, false);
            holder = new EventsViewHolder(eventView, R.id.eventElement, true, true);
            convertView = eventView;
            convertView.setTag(holder);
        } else {
            holder = (EventsViewHolder) convertView.getTag();
        }

        holder.setModel(events.get(position).getEvent(), false);
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
}
