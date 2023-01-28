package amai.org.conventions.events.holders;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.OnClickAnimationListener;
import amai.org.conventions.events.EventView;
import amai.org.conventions.events.adapters.ListPagerAdapter;
import amai.org.conventions.events.listeners.OnSwipeListener;
import amai.org.conventions.model.ConventionEvent;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

/**
 * ViewHolder for an event view that allow swipe to add/remove from favorites
 */
public class SwipeableEventViewHolder extends RecyclerView.ViewHolder {

	private final static int MAIN_VIEW_POSITION = 1;
	private ViewPager viewPager;
	private EventView mainEventView;
	private View hiddenView;
	private View hiddenView2;
	private ViewPager.OnPageChangeListener listener;
	private ConventionEvent event;

	public SwipeableEventViewHolder(View itemView) {
		super(itemView);

		viewPager = (ViewPager) itemView.findViewById(R.id.swipe_pager);
		mainEventView = createEventView(viewPager.getContext(), R.id.main_event_view);
		hiddenView = createHiddenView(viewPager.getContext(), R.id.hidden_event_view);
		hiddenView2 = createHiddenView(viewPager.getContext(), R.id.hidden_event_view2);

		List<View> views = new ArrayList<>(3);
		views.add(hiddenView);
		views.add(mainEventView);
		views.add(hiddenView2);

		viewPager.setCurrentItem(MAIN_VIEW_POSITION, false);
		viewPager.setOffscreenPageLimit(views.size() - 1);
		viewPager.setAdapter(new ListPagerAdapter(views));

		OnClickAnimationListener listener = new OnClickAnimationListener();
		mainEventView.setOnFavoritesButtonClickedListener(listener);
	}

	private EventView createEventView(Context context, int id) {
		EventView eventView = new EventView(context);
		eventView.setId(id);
		ViewPager.LayoutParams params = new ViewPager.LayoutParams();
		params.width = ViewGroup.LayoutParams.MATCH_PARENT;
		params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		eventView.setLayoutParams(params);
		return eventView;
	}

	private View createHiddenView(Context context, int id) {
		View view = new View(context);
		view.setId(id);
		ViewPager.LayoutParams params = new ViewPager.LayoutParams();
		params.width = ViewGroup.LayoutParams.MATCH_PARENT;
		params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		view.setLayoutParams(params);
		view.setVisibility(View.GONE);
		return view;
	}

	public ConventionEvent getModel() {
		return event;
	}

	public void setModel(ConventionEvent event) {
		setModel(event, false);
	}

	public void setModel(ConventionEvent event, boolean conflicting) {
		this.reset();

		this.event = event;
		setEventInEventView(mainEventView, event, conflicting);
	}

	private void setEventInEventView(EventView view, ConventionEvent event, boolean conflicting) {
		view.setShowFavoriteIcon(true);
		view.setShowHallName(true);
		view.setEvent(event, conflicting);
	}

	public void setOnViewSwipedAction(final OnEventSwipedListener action) {
		removeOnPageChangeListener();

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				action.onEventSwiped(event);
			}
		};
		this.listener = new OnSwipeListener(viewPager, MAIN_VIEW_POSITION, runnable);
		viewPager.clearOnPageChangeListeners();
		viewPager.addOnPageChangeListener(listener);
	}

	public void reset() {
		removeOnPageChangeListener();
		viewPager.setCurrentItem(MAIN_VIEW_POSITION, false);
	}

	private void removeOnPageChangeListener() {
		if (listener != null) {
			viewPager.removeOnPageChangeListener(listener);
		}
	}

	public static interface OnEventSwipedListener {
		void onEventSwiped(ConventionEvent event);
	}
}
