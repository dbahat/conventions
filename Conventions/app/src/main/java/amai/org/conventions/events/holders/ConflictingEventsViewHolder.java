package amai.org.conventions.events.holders;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
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
        configureListSize();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                int initialHeight = eventsListView.getLayoutParams().height;
                configureListSize();

                ValueAnimator anim = ValueAnimator.ofInt(initialHeight, eventsListView.getLayoutParams().height);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int val = (Integer) valueAnimator.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = eventsListView.getLayoutParams();
                        layoutParams.height = val;
                        eventsListView.setLayoutParams(layoutParams);
                    }
                });
                anim.start();
            }
        });
    }

    private void configureListSize() {
        // Set height - must be calculated at runtime since wrap_content does not work for recycler view inside recycler view
        Resources resources = eventsListView.getResources();
        float dpAsPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, resources.getDisplayMetrics());

        // Leaving 2dp offset for the card view boarders
        eventsListView.getLayoutParams().height = (int) dpAsPixels + adapter.getItemCount() * (int) (resources.getDimension(R.dimen.event_height) + dpAsPixels);
    }
}
