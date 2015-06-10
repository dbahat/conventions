package amai.org.conventions.events.holders;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.EventView;
import amai.org.conventions.model.ConventionEvent;

/**
 * ViewHolder for an event view that allow swipe to add/remove from favorites
 */
public class SwipeableEventViewHolder extends RecyclerView.ViewHolder {

    private EventView mainEventView;
    private EventView hiddenEventView;

    private ViewPager viewPager;
	private List<View> views;
    private ViewPager.OnPageChangeListener listener;

	private int mainViewPosition = 0;

	public SwipeableEventViewHolder(View itemView) {
        super(itemView);

        viewPager = (ViewPager) itemView.findViewById(R.id.swipe_pager);
		mainEventView = (EventView) itemView.findViewById(R.id.main_event_view);

	    views = new ArrayList<>(2); // 2 = maximum number of views in the list
	    views.add(mainEventView);

        hiddenEventView = (EventView) itemView.findViewById(R.id.hidden_event_view);
        views.add(0, hiddenEventView);
        mainViewPosition = 1;
        viewPager.setCurrentItem(mainViewPosition, false);

		viewPager.setOffscreenPageLimit(views.size() - 1);

	    viewPager.setAdapter(new PagerAdapter() {
		    @Override
		    public int getCount() {
			    return views.size();
		    }

		    @Override
		    public boolean isViewFromObject(View view, Object object) {
			    return view == object;
		    }

		    @Override
		    public Object instantiateItem(ViewGroup container, int position) {
			    View view = views.get(position);
			    container.addView(view);
			    return view;
		    }
	    });
    }


	public void setModel(ConventionEvent event) {
        setModel(event, false);
    }

    public void setModel(ConventionEvent event, boolean conflicting) {
        mainEventView.setEvent(event);
        mainEventView.setShowFavoriteIcon(true);
        mainEventView.setShowHallName(true);
        mainEventView.setConflicting(conflicting);

        if (hiddenEventView != null) {
            hiddenEventView.setEvent(event);
            hiddenEventView.setShowFavoriteIcon(true);
            hiddenEventView.setShowHallName(true);
            hiddenEventView.setConflicting(conflicting);

            // Set the hidden swipe layout event to have the opposite attending icon state, so that swiping will
            // feel like changing the attending state.
            hiddenEventView.setAttending(!event.isAttending());
        }
    }

    public void setOnViewSwipedAction(final Runnable action) {
	    removeOnPageChangeListener();

	    this.listener = new ViewPager.OnPageChangeListener() {
	        boolean pageSelected = false;

	        @Override
	        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	        }

	        @Override
	        public void onPageSelected(int p) {
		        // Only keep track that the page changed since this method is called before the scroll finished
		        // so we can't perform UI updates here (because it will be immediate and the event view will appear
		        // to be stuck)
		        pageSelected = true;
	        }

	        @Override
	        public void onPageScrollStateChanged(int state) {
		        // If the page was selected (attending status changed) and scrolling has finished,
		        // perform the actual action
		        if (state != ViewPager.SCROLL_STATE_IDLE || !pageSelected) {
			        return;
		        }
		        pageSelected = false;
		        action.run();

		        // Reset the layout state. Remove the listener so it isn't called when the current item is reset.
		        viewPager.removeOnPageChangeListener(this);
		        viewPager.setCurrentItem(mainViewPosition, false);
		        viewPager.addOnPageChangeListener(this);
	        }
        };

	    viewPager.addOnPageChangeListener(listener);
    }

    public void reset() {
	    removeOnPageChangeListener();
	    viewPager.setCurrentItem(mainViewPosition, false);
    }

	private void removeOnPageChangeListener() {
		if (listener != null) {
		    viewPager.removeOnPageChangeListener(listener);
		}
	}
}
