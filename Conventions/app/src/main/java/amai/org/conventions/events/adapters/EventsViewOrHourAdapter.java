package amai.org.conventions.events.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.holders.EventTimeViewHolder;
import amai.org.conventions.events.holders.EventsViewHolder;
import amai.org.conventions.model.ConventionEvent;

public class EventsViewOrHourAdapter extends BaseAdapter {

    private static final int CONVENTION_EVENT_VIEW_TYPE = 0;
    private static final int HOUR_VIEW_TYPE = 1;

    private List eventsOrHours;

    public EventsViewOrHourAdapter(List eventsOrHours) {
        this.eventsOrHours = eventsOrHours;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case CONVENTION_EVENT_VIEW_TYPE:
                View eventView = LayoutInflater.from(parent.getContext()).inflate(R.layout.convention_event_view_holder, parent, false);
                return new EventsViewHolder(eventView, R.id.eventElement, true, true);
            case HOUR_VIEW_TYPE:
                View eventTimeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.convention_event_time, parent, false);
                return new EventTimeViewHolder(eventTimeView);
            default:
                throw new AssertionError("Undefined viewType: " + viewType);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EventsViewHolder) {
            EventsViewHolder eventViewHolder = (EventsViewHolder) holder;
            ConventionEvent conventionEvent = (ConventionEvent) eventsOrHours.get(position);
            eventViewHolder.setModel(conventionEvent, false);
        }
        else if (holder instanceof EventTimeViewHolder) {
            EventTimeViewHolder eventTimeViewHolder = (EventTimeViewHolder) holder;
            Date date = (Date) eventsOrHours.get(position);
            eventTimeViewHolder.setTime(date);
        }
    }

	@Override
	public int getCount() {
		return eventsOrHours.size();
	}

	@Override
	public Object getItem(int position) {
		return eventsOrHours.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RecyclerView.ViewHolder holder = null;
		int type = getItemViewType(position);
		System.out.println("getView " + position + " " + convertView + " type = " + type);
		if (convertView == null) {
			holder = onCreateViewHolder(parent, type);
			convertView = holder.itemView;
			convertView.setTag(holder);
		} else {
			holder = (RecyclerView.ViewHolder)convertView.getTag();
		}
		onBindViewHolder(holder, position);;
		return convertView;
	}

	@Override
    public int getItemViewType(int position) {
        Object eventOrHour = eventsOrHours.get(position);
        if (eventOrHour instanceof ConventionEvent) {
            return CONVENTION_EVENT_VIEW_TYPE;
        }

        if (eventOrHour instanceof Date) {
            return HOUR_VIEW_TYPE;
        }

        throw new AssertionError("The following type is not ConventionEvent on Date object: " + eventOrHour.getClass().getSimpleName());
    }

	@Override
	public int getViewTypeCount() {
		return 2;
	}
}
