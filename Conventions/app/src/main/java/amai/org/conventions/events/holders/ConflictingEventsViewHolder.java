package amai.org.conventions.events.holders;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import amai.org.conventions.R;
import amai.org.conventions.events.adapters.DismissibleEventsViewAdapter;
import amai.org.conventions.model.ConventionEvent;

public class ConflictingEventsViewHolder extends RecyclerView.ViewHolder {
    private final RecyclerView eventsListView;
    private final Context context;
    private DismissibleEventsViewAdapter adapter;

    public ConflictingEventsViewHolder(View itemView, Context context) {
        super(itemView);
        eventsListView = (RecyclerView) itemView.findViewById(R.id.conflictingEventsList);
        this.context = context;
    }

    public void setModel(final ArrayList<ConventionEvent> events) {
        adapter = new DismissibleEventsViewAdapter(events, true);
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

    private void updateListHeight() {
        // Set height - must be calculated at runtime since wrap_content does not work for recycler view inside recycler view
	    updateViewHeight(getListHeight(eventsListView.getResources(), adapter.getItemCount()), eventsListView);
    }

	private static int getListHeight(Resources resources, int items) {
		float dpAsPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, resources.getDisplayMetrics());

		// Leaving 2dp offset for the card view boarders
		return (int) dpAsPixels + items * (int) (resources.getDimension(R.dimen.event_height) + dpAsPixels);
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
}
