package amai.org.conventions;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import amai.org.conventions.model.Colors;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.Dates;

public class EventView extends FrameLayout {
    private final ImageView faveIcon;
    private final TextView hallName;
    private final TextView startTime;
    private final TextView endTime;
    private final TextView eventName;
    private final TextView lecturerName;
    private final ViewGroup timeLayout;
    private final ViewGroup eventDescription;

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private ConventionEvent event = null;

    public EventView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(this.getContext()).inflate(R.layout.convention_event, this, true);

        timeLayout = (ViewGroup) this.findViewById(R.id.timeLayout);
        faveIcon = (ImageView) this.findViewById(R.id.faveIcon);
        hallName = (TextView) this.findViewById(R.id.hallName);
        startTime = (TextView) this.findViewById(R.id.startTime);
        endTime = (TextView) this.findViewById(R.id.endTime);
        eventName = (TextView) this.findViewById(R.id.eventName);
        lecturerName = (TextView) this.findViewById(R.id.lecturerName);
        eventDescription = (ViewGroup) this.findViewById(R.id.eventDescription);

	    // TODO swipe on the entire view instead of click on the timeLayout, and handle the
	    // event from the fragment or give an option of how to behave
        timeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConventionEvent event = EventView.this.event;
                if (event != null) {
                    event.setAttending(!event.isAttending());
                    EventView.this.setEvent(event);
                }
            }
        });

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
        setShowFavoriteIcon(params.getBoolean(R.styleable.Event_showFavoriteIcon, true));
        setStartTime(params.getString(R.styleable.Event_startTime));
        setEndTime(params.getString(R.styleable.Event_endTime));
        setEventTitle(params.getString(R.styleable.Event_eventTitle));
        setLecturerName(params.getString(R.styleable.Event_lecturerName));

        setColorFromAttributes(timeLayout, params, R.styleable.Event_eventTypeColor);
        setColorFromAttributes(eventDescription, params, R.styleable.Event_eventColor);

        params.recycle();
    }

    public void setEvent(ConventionEvent event) {
        this.event = event;

        setColorsFromEvent(event);
        setAttending(event.isAttending());
        setHallName(event.getHall().getName());
        setStartTime(timeFormat.format(event.getStartTime()));
        setEndTime(timeFormat.format(event.getEndTime()));
        setEventTitle(event.getTitle());
        setLecturerName(event.getLecturer());
    }

    private void setColorsFromEvent(ConventionEvent event) {
        Date now = Dates.now();
        int color = event.getType().getBackgroundColor();
        if (event.getStartTime().after(now)) {
            setEventTypeColor(color);
	        setEventColor(Colors.WHITE);
        } else if (event.getEndTime().before(now)) {
            setEventTypeColor(color);
	        setEventColor(Colors.VERY_LIGHT_GRAY);
        } else {
            setEventTypeColor(color);
            setEventColor(Colors.GOLD);
        }
    }

    private void setColorFromAttributes(ViewGroup layout, TypedArray params, int drawableOrColorId) {
        Drawable drawable = params.getDrawable(drawableOrColorId);
        if (drawable != null) {
            setLayoutColor(layout, drawable);
        } else {
            setLayoutColor(layout, params.getColor(drawableOrColorId, Color.WHITE));
        }
    }

    public void setEventTypeColor(Drawable drawable) {
        setLayoutColor(timeLayout, drawable);
    }

    public void setEventTypeColor(int color) {
        setLayoutColor(timeLayout, color);
    }

    public void setEventColor(int color) {
        setLayoutColor(eventDescription, color);
    }

    public void setEventColor(Drawable drawable) {
        setLayoutColor(eventDescription, drawable);
    }

    private void setLayoutColor(ViewGroup layout, Drawable drawable) {
        setBackground(layout, drawable);
    }

    private void setLayoutColor(ViewGroup layout, int color) {
        layout.setBackgroundColor(color);
    }

    public void setAttending(boolean isAttending) {
        Resources resources = getContext().getResources();
        int favorite_icon = isAttending ? R.drawable.favorite_icon_true : R.drawable.favorite_icon_false;
        faveIcon.setImageDrawable(resources.getDrawable(favorite_icon));
    }

    public void setShowHallName(boolean show) {
        hallName.setVisibility(show ? VISIBLE : GONE);
    }

    public void setShowFavoriteIcon(boolean show) {
        faveIcon.setVisibility(show ? VISIBLE : GONE);
    }

    protected void setHallName(String name) {
        hallName.setText(name);
    }

    protected void setStartTime(String formattedStartTime) {
        startTime.setText(formattedStartTime);
    }

    public void setEndTime(String formattedEndTime) {
        endTime.setText(formattedEndTime);
    }

    protected void setEventTitle(String name) {
        eventName.setText(name);
    }

    protected void setLecturerName(String name) {
        lecturerName.setText(name);
    }

    private void setBackground(ViewGroup layout, Drawable drawable) {
        int pL = layout.getPaddingLeft();
        int pT = layout.getPaddingTop();
        int pR = layout.getPaddingRight();
        int pB = layout.getPaddingBottom();

        layout.setBackground(drawable);
        layout.setPadding(pL, pT, pR, pB);
    }
}
