package amai.org.conventions;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
        private TextView eventNameView;

        public EventsViewHolder(View itemView) {
            super(itemView);
            eventNameView = (TextView) itemView.findViewById(R.id.eventName);
        }

        public void setModel(ConventionEvent event) {
            eventNameView.setText(event.getTitle());
        }
    }
}
