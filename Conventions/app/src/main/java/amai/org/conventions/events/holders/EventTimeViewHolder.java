package amai.org.conventions.events.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import sff.org.conventions.R;
import amai.org.conventions.utils.Dates;

public class EventTimeViewHolder extends RecyclerView.ViewHolder {
    private TextView timeTextView;
    private int currentHour;

    public EventTimeViewHolder(View itemView) {
        super(itemView);

        timeTextView = (TextView) itemView.findViewById(R.id.event_time_text_view);
    }

    public void setTime(Date date) {
        setTime(date, "HH:SS");
    }

    public void setTime(Date date, String timeFormat) {
        timeTextView.setText(new SimpleDateFormat(timeFormat, Dates.getLocale()).format(date));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getCurrentHour() {
        return currentHour;
    }
}
