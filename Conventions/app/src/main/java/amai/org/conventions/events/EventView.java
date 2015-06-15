package amai.org.conventions.events;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
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
    private final CardView eventContainer;

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
        eventContainer = (CardView) this.findViewById(R.id.eventContainer);

        setConflicting(false);
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
        setColorsFromEvent(event);
        setAttending(event.isAttending());
        setHallName(event.getHall().getName());
        setStartTime(Dates.formatHoursAndMinutes(event.getStartTime()));
        setEndTime(Dates.formatHoursAndMinutes(event.getEndTime()));
        setEventTitle(event.getTitle());
        setLecturerName(event.getLecturer());

        // Setting the event id inside the view tag, so we can easily extract it from the view when listeneing to onClick events.
        setTag(event.getId());
    }

    private void setColorsFromEvent(ConventionEvent event) {
        Date now = Dates.now();
        int colorAttrId = event.getType().getBackgroundColorAttributeId();
        setEventTypeColor(ThemeAttributes.getColor(getContext(), colorAttrId));
        if (event.getStartTime().after(now)) {
            setEventColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeNotStartedColor));
        } else if (event.getEndTime().before(now)) {
            setEventColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeEndedColor));
        } else {
            setEventColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeCurrentColor));
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
        int favorite_icon = isAttending ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off;
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

    public void setConflicting(boolean conflicting) {
        if (conflicting) {
            eventContainer.setCardElevation(0.0f);
            eventContainer.setMaxCardElevation(0.0f);
            eventContainer.setCardBackgroundColor(ThemeAttributes.getColor(getContext(), R.attr.conflictingEventsBackground));
        } else {
            eventContainer.setCardElevation(6.0f);
            eventContainer.setMaxCardElevation(6.0f);
            eventContainer.setCardBackgroundColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeNotStartedColor));
        }
    }
}
