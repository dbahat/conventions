package amai.org.conventions.events.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import sff.org.conventions.R;
import amai.org.conventions.events.DefaultEventFavoriteChangedListener;
import amai.org.conventions.events.holders.EventViewHolder;
import amai.org.conventions.events.listeners.OnEventFavoriteChangedListener;
import amai.org.conventions.model.ConventionEvent;

public class EventsViewAdapter extends RecyclerView.Adapter<EventViewHolder> {
	private List<ConventionEvent> eventsList;
	private OnEventFavoriteChangedListener listener;

	public EventsViewAdapter(List<ConventionEvent> eventsList, View recyclerView) {
		this.eventsList = eventsList;
		this.listener = new DefaultEventFavoriteChangedListener(recyclerView);
	}

	@Override
	public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.convention_event_view_holder, parent, false);
		return new EventViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final EventViewHolder holder, int position) {
		final ConventionEvent event = eventsList.get(position);
		holder.setModel(event);

		holder.getEventView().setOnFavoritesButtonClickedListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onEventFavoriteChanged(event);
			}
		});
	}

	@Override
	public int getItemCount() {
		return eventsList.size();
	}
}
