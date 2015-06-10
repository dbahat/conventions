package amai.org.conventions.events.adapters;

import android.support.v7.widget.RecyclerView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import java.util.List;

import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;

public class EventSwipeToDismissListener extends SimpleSwipeListener {

	private int position;
	private ConventionEvent event;
	private List<?> eventsList;
	private RecyclerView.Adapter<?> adapter;

	public EventSwipeToDismissListener(int position, ConventionEvent event, List<?> eventsList, RecyclerView.Adapter<?> adapter) {
		this.position = position;
		this.event = event;
		this.eventsList = eventsList;
		this.adapter = adapter;

		((ChangingDatasetAdapter) adapter).addOnDatasetChangedListener(new OnDatasetChangedListener() {
			@Override
			public void onItemRemoved(int position) {
				if (position < EventSwipeToDismissListener.this.position) {
					EventSwipeToDismissListener.this.position--;
				}
			}
		});
	}

	@Override
	public void onOpen(SwipeLayout layout) {
		super.onOpen(layout);

		event.setAttending(false);
		Convention.getInstance().save();

		eventsList.remove(position);
		adapter.notifyItemRemoved(position);

		for (OnDatasetChangedListener listener : ((ChangingDatasetAdapter) adapter).getOnDatasetChangedListeners()) {
			listener.onItemRemoved(position);
		}
	}

	@Override
	public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
		super.onUpdate(layout, leftOffset, topOffset);
		float progress = 1 - (leftOffset * 1.5f / (float) layout.getMeasuredWidth());
		layout.setAlpha(progress);
	}

	public interface OnDatasetChangedListener {
		void onItemRemoved(int position);
	}

	public interface ChangingDatasetAdapter {
		void addOnDatasetChangedListener(OnDatasetChangedListener listener);
		List<OnDatasetChangedListener> getOnDatasetChangedListeners();
	}
}
