package amai.org.conventions;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import amai.org.conventions.model.ConventionEvent;

public class EventsViewAdapter extends RecyclerView.Adapter<EventsViewAdapter.EventsViewHolder> {
    private ArrayList<ConventionEvent> eventsList;

    public EventsViewAdapter(ArrayList<ConventionEvent> eventsList) {
        this.eventsList = eventsList;
    }

    @Override
    public EventsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.convention_event, viewGroup, false);
        return new EventsViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    @Override
    public void onBindViewHolder(EventsViewHolder eventsViewHolder, int i) {
        eventsViewHolder.setModel(eventsList.get(i));
    }

    public class EventsViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout eventContainer;
        private final ImageView faveIconEnabled;
        private final ImageView faveIconDisabled;
        private final TextView hallName;
        private final TextView startTime;
        private final TextView endTime;
        private final TextView eventName;
        private final TextView lecturerName;

        public EventsViewHolder(View itemView) {
            super(itemView);
            eventContainer = (LinearLayout) itemView.findViewById(R.id.eventContainer);
            faveIconEnabled = (ImageView) itemView.findViewById(R.id.faveIconEnabled);
            faveIconDisabled = (ImageView) itemView.findViewById(R.id.faveIconDisabled);
            hallName = (TextView) itemView.findViewById(R.id.hallName);
            startTime = (TextView) itemView.findViewById(R.id.startTime);
            endTime = (TextView) itemView.findViewById(R.id.endTime);
            eventName = (TextView) itemView.findViewById(R.id.eventName);
            lecturerName = (TextView) itemView.findViewById(R.id.lecturerName);
        }

        public void setModel(ConventionEvent event) {
            eventContainer.setBackgroundColor(event.getType().getBackgroundColor());
            faveIconEnabled.setVisibility(event.isAttending() ? View.VISIBLE : View.GONE);
            faveIconDisabled.setVisibility(event.isAttending() ? View.GONE : View.VISIBLE);
            hallName.setText(event.getHall().getName());

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            startTime.setText(sdf.format(event.getStartTime()));
            endTime.setText(sdf.format(event.getEndTime()));
            eventName.setText(event.getTitle());
            lecturerName.setText(event.getLecturer());
        }
    }
}
