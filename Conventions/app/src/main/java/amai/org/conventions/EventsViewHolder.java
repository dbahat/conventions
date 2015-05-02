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

    public EventsViewHolder(View itemView, int eventElementId) {
        super(itemView);
        eventView = (EventView) itemView.findViewById(eventElementId);
    }

    public void setModel(ConventionEvent event) {
        eventView.setEvent(event);
    }
}
