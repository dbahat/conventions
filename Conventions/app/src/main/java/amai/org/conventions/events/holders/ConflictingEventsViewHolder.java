package amai.org.conventions.events.holders;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;

import amai.org.conventions.R;
import amai.org.conventions.events.adapters.EventsViewAdapter;
import amai.org.conventions.model.ConventionEvent;

public class ConflictingEventsViewHolder extends RecyclerView.ViewHolder {
    private final RecyclerView eventsListView;
	private final Context context;
	private boolean showFavoriteIcon;
    private boolean showHallName;

    public ConflictingEventsViewHolder(View itemView, boolean showFavoriteIcon, boolean showHallName, Context context) {
        super(itemView);
        this.showFavoriteIcon = showFavoriteIcon;
        this.showHallName = showHallName;
        eventsListView = (RecyclerView) itemView.findViewById(R.id.conflictingEventsList);
	    this.context = context;
    }

    public void setModel(ArrayList<ConventionEvent> events) {
	    eventsListView.setAdapter(new EventsViewAdapter(events, showFavoriteIcon, showHallName, true));
	    eventsListView.setLayoutManager(new LinearLayoutManager(context));

	    // Set height - must be calculated at runtime since wrap_content does not work for recycler view inside recycler view
	    Resources resources = eventsListView.getResources();
	    float dpAsPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, resources.getDisplayMetrics());
	    eventsListView.getLayoutParams().height = (int) dpAsPixels + events.size() * (int) (resources.getDimension(R.dimen.event_height) + dpAsPixels);

    }
}
