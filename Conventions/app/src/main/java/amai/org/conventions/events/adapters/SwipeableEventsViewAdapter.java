package amai.org.conventions.events.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.DefaultEventFavoriteChangedListener;
import amai.org.conventions.events.holders.SwipeableEventViewHolder;
import amai.org.conventions.events.listeners.OnEventFavoriteChangedListener;
import amai.org.conventions.model.ConventionEvent;

public class SwipeableEventsViewAdapter extends RecyclerView.Adapter<SwipeableEventViewHolder> {
	private List<ConventionEvent> eventsList;

	private List<String> keywordsToHighlight;
	private OnEventFavoriteChangedListener listener;

	public SwipeableEventsViewAdapter(List<ConventionEvent> eventsList, View recyclerView) {
		this.eventsList = eventsList;
		this.listener = new DefaultEventFavoriteChangedListener(recyclerView);
	}

	@Override
	public SwipeableEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipeable_event_view, parent, false);
		return new SwipeableEventViewHolder(view, false);
	}

	@Override
	public void onBindViewHolder(final SwipeableEventViewHolder holder, int position) {
		holder.reset();
		final ConventionEvent event = eventsList.get(position);
		holder.setModel(event, false);

		if (keywordsToHighlight != null) {
			holder.setKeywordsHighlighting(keywordsToHighlight);
		}

		holder.setOnViewSwipedAction(new Runnable() {
			@Override
			public void run() {
				listener.onEventFavoriteChanged(event);
			}
		});
	}

	@Override
	public int getItemCount() {
		return eventsList.size();
	}

	public void setEventsList(List<ConventionEvent> eventsList) {
		this.eventsList = eventsList;
		notifyDataSetChanged();
	}

	public void setKeywordsHighlighting(List<String> keywords) {
		keywordsToHighlight = keywords;
	}
}
