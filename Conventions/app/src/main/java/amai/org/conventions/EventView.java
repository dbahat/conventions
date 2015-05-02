package amai.org.conventions;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import amai.org.conventions.model.ConventionEvent;

public class EventView extends FrameLayout {
    private final CardView eventContainer;
    private final ImageView faveIconEnabled;
    private final ImageView faveIconDisabled;
    private final TextView hallName;
    private final TextView startTime;
    private final TextView endTime;
    private final TextView eventName;
    private final TextView lecturerName;

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public EventView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(this.getContext()).inflate(R.layout.convention_event, this, true);

        eventContainer = (CardView) this.findViewById(R.id.eventContainer);
        faveIconEnabled = (ImageView) this.findViewById(R.id.faveIconEnabled);
        faveIconDisabled = (ImageView) this.findViewById(R.id.faveIconDisabled);
        hallName = (TextView) this.findViewById(R.id.hallName);
        startTime = (TextView) this.findViewById(R.id.startTime);
        endTime = (TextView) this.findViewById(R.id.endTime);
        eventName = (TextView) this.findViewById(R.id.eventName);
        lecturerName = (TextView) this.findViewById(R.id.lecturerName);

        setAttributes(attrs);
    }

    private void setAttributes(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray params = getContext().obtainStyledAttributes(attrs, R.styleable.Event);
        setAttending(params.getBoolean(R.styleable.Event_attending, false));
        setHallName(params.getString(R.styleable.Event_hallName));
        setShowHallName(params.getBoolean(R.styleable.Event_showHallName, true));
        setStartTime(params.getString(R.styleable.Event_startTime));
        setEndTime(params.getString(R.styleable.Event_endTime));
        setEventTitle(params.getString(R.styleable.Event_eventTitle));
        setLecturerName(params.getString(R.styleable.Event_lecturerName));
        params.recycle();
    }

    public void setEvent(ConventionEvent event) {
        setBackgroundColor(event.getType().getBackgroundColor());
        setAttending(event.isAttending());
        setHallName(event.getHall().getName());
        setStartTime(timeFormat.format(event.getStartTime()));
        setEndTime(timeFormat.format(event.getEndTime()));
        setEventTitle(event.getTitle());
        setLecturerName(event.getLecturer());
    }

    public void setBackgroundColor(int color) {
        eventContainer.setCardBackgroundColor(color);
    }

    public void setAttending(boolean isAttending) {
        faveIconEnabled.setVisibility(isAttending ? View.VISIBLE : View.GONE);
        faveIconDisabled.setVisibility(isAttending ? View.GONE : View.VISIBLE);
    }

    protected void setShowHallName(boolean show) {
        hallName.setVisibility(show ? VISIBLE : GONE);
    }

    protected void setHallName(String name) {
        hallName.setText(name);
    }

    protected void setStartTime(String formattedStartTime) {
        startTime.setText(formattedStartTime);
    }

    protected void setEndTime(String formattedEndTime) {
        endTime.setText(formattedEndTime);
    }

    protected void setEventTitle(String name) {
        eventName.setText(name);
    }

    protected void setLecturerName(String name) {
        lecturerName.setText(name);
    }
}
