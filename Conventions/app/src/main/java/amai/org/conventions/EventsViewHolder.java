package amai.org.conventions;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import amai.org.conventions.model.ConventionEvent;

public class EventsViewHolder extends RecyclerView.ViewHolder {
    private final EventView eventView;
    private boolean showFavoriteIcon;
    private boolean showHallName;

    public EventsViewHolder(View itemView, int eventElementId, boolean showFavoriteIcon, boolean showHallName) {
        super(itemView);
        this.showFavoriteIcon = showFavoriteIcon;
        this.showHallName = showHallName;
        eventView = (EventView) itemView.findViewById(eventElementId);
    }

    public void setModel(ConventionEvent event) {
        eventView.setEvent(event);
        eventView.setShowFavoriteIcon(showFavoriteIcon);
        eventView.setShowHallName(showHallName);
    }
}
