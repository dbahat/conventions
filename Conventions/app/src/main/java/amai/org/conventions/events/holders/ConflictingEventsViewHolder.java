package amai.org.conventions.events.holders;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.EventView;
import amai.org.conventions.events.adapters.DismissibleEventsViewAdapter;
import amai.org.conventions.model.ConventionEvent;
import sff.org.conventions.R;

public class ConflictingEventsViewHolder extends RecyclerView.ViewHolder {
	private static int eventViewHeight = -1;
	private final RecyclerView eventsListView;
	private final Context context;
	private DismissibleEventsViewAdapter adapter;
	private RecyclerView.AdapterDataObserver eventRemovedListener;

	public ConflictingEventsViewHolder(View itemView, Context context) {
		super(itemView);
		eventsListView = (RecyclerView) itemView.findViewById(R.id.conflictingEventsList);
		this.context = context;
		if (eventViewHeight < 0) {
			eventViewHeight = calculateEventViewHeight();
		}
		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
				DividerItemDecoration.VERTICAL);
		dividerItemDecoration.setDrawable(ThemeAttributes.getDrawable(context, R.attr.conflictingEventListDivider));
		eventsListView.addItemDecoration(dividerItemDecoration);
	}

	private static Animator getHeightChangeAnimator(final View view, int initialHeight, int targetHeight) {
		ValueAnimator animator = ValueAnimator.ofInt(initialHeight, targetHeight);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				updateViewHeight((Integer) valueAnimator.getAnimatedValue(), view);
			}
		});
		return animator;
	}

	private static void updateViewHeight(int height, View view) {
		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		layoutParams.height = height;
		view.setLayoutParams(layoutParams);
	}

	private int calculateEventViewHeight() {
		Point screenSize = getScreenSize();
		EventView eventView = new EventView(context);
		eventView.setEvent(null, true);
		eventView.measure(screenSize.x, screenSize.y);
		return eventView.getMeasuredHeight();
	}

	private Point getScreenSize() {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	public List<ConventionEvent> getModel() {
		return adapter.getEventsList();
	}

	public void setModel(final ArrayList<ConventionEvent> events) {
		if (adapter != null && eventRemovedListener != null) {
			adapter.unregisterAdapterDataObserver(eventRemovedListener);
			eventRemovedListener = null;
		}
		adapter = new DismissibleEventsViewAdapter(events);
		eventsListView.setAdapter(adapter);
		eventsListView.setLayoutManager(new LinearLayoutManager(context));
		updateListHeight();

		// Add animation for view height change after the item is removed (it will run at the same time
		// as the other items moving to fill the space).
		// The only way to make them start at the exact same time is as used here (set it to start with a delay
		// of remove duration while remove is starting). Other things tried: animate right when remove is finished,
		// animate right when move is starting. Both didn't start at the exact right time.
		eventsListView.setItemAnimator(new DefaultItemAnimator() {

			@Override
			public void onRemoveStarting(RecyclerView.ViewHolder item) {
				super.onRemoveStarting(item);

				ViewCompat.postOnAnimationDelayed(item.itemView, new Runnable() {
					@Override
					public void run() {
						int initialHeight = eventsListView.getLayoutParams().height;
						int targetHeight = getListHeight(eventsListView.getResources(), adapter.getItemCount());

						Animator animator = getHeightChangeAnimator(eventsListView, initialHeight, targetHeight);
						animator.setDuration(getMoveDuration()).start();
					}
				}, getRemoveDuration());
			}
		});
	}

	public void setEventRemovedListener(final OnEventListChangedListener listener) {
		if (eventRemovedListener != null) {
			adapter.unregisterAdapterDataObserver(eventRemovedListener);
			eventRemovedListener = null;
		}

		this.eventRemovedListener = new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				super.onItemRangeRemoved(positionStart, itemCount);
				listener.onEventRemoved();
			}

			@Override
			public void onChanged() {
				super.onChanged();
				listener.onEventListChanged();
			}
		};
		adapter.registerAdapterDataObserver(eventRemovedListener);
	}

	private void updateListHeight() {
		// Set height - must be calculated at runtime since wrap_content does not work for recycler view inside recycler view
		updateViewHeight(getListHeight(eventsListView.getResources(), adapter.getItemCount()), eventsListView);
	}

	private int getListHeight(Resources resources, int items) {
		return (items * eventViewHeight) + (getDividerHeight(resources) * (items > 0 ? items - 1 : 0));
	}

	private int getDividerHeight(Resources resources) {
		return resources.getDimensionPixelSize(R.dimen.event_divider_height);
	}

	public static interface OnEventListChangedListener {
		void onEventRemoved();
		void onEventListChanged();
	}
}
