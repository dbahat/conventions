package amai.org.conventions.events.adapters;

import android.support.v7.widget.RecyclerView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import java.util.List;

import amai.org.conventions.events.holders.DismissibleEventViewHolder;
import amai.org.conventions.model.Convention;

public class EventSwipeToDismissListener extends SimpleSwipeListener {

	private DismissibleEventViewHolder viewHolder;
	private List<?> eventsList;
	private RecyclerView.Adapter<?> adapter;

	public EventSwipeToDismissListener(DismissibleEventViewHolder viewHolder, List<?> eventsList, RecyclerView.Adapter<?> adapter) {
		this.viewHolder = viewHolder;
		this.eventsList = eventsList;
		this.adapter = adapter;
	}

	@Override
	public void onOpen(SwipeLayout layout) {
		super.onOpen(layout);

		viewHolder.getModel().setAttending(false);
		Convention.getInstance().save();

		eventsList.remove(viewHolder.getAdapterPosition());
		adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
	}

	@Override
	public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
		super.onUpdate(layout, leftOffset, topOffset);
		float progress = 1 - (leftOffset * 1.5f / (float) layout.getMeasuredWidth());
		layout.setAlpha(progress);
	}
}
