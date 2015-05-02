package amai.org.conventions;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private final LinearLayout timeLayout;

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public EventView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(this.getContext()).inflate(R.layout.convention_event, this, true);

        timeLayout = (LinearLayout) this.findViewById(R.id.timeLayout);
        faveIcon = (ImageView) this.findViewById(R.id.faveIcon);
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
        setColor(params.getColor(R.styleable.Event_eventColor, Color.WHITE), null);
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
        setColor(event.getType().getBackgroundColor(), event);
        setAttending(event.isAttending());
        setHallName(event.getHall().getName());
        setStartTime(timeFormat.format(event.getStartTime()));
        setEndTime(timeFormat.format(event.getEndTime()));
        setEventTitle(event.getTitle());
        setLecturerName(event.getLecturer());
    }

    public void setColor(int color, ConventionEvent event) {
        Date now = Dates.now();
        if (event == null || event.getStartTime().after(now)) {
            timeLayout.setBackgroundColor(color);
        } else if (event.getEndTime().before(now)) {
            timeLayout.setBackgroundColor(Colors.fade(color));
        } else {
            GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{Colors.fade(color), color});
            setBackground(timeLayout, gradient);
        }
    }

    public void setAttending(boolean isAttending) {
        Resources resources = getContext().getResources();
        int favorite_icon = isAttending ? R.drawable.favorite_icon : R.drawable.favorite_icon_gray;
        faveIcon.setImageDrawable(resources.getDrawable(favorite_icon));
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

    private void setBackground(LinearLayout layout, Drawable drawable) {
        int pL = layout.getPaddingLeft();
        int pT = layout.getPaddingTop();
        int pR = layout.getPaddingRight();
        int pB = layout.getPaddingBottom();

        layout.setBackground(drawable);
        layout.setPadding(pL, pT, pR, pB);
    }
}
