package amai.org.conventions;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventTimeViewHolder extends RecyclerView.ViewHolder {
    private TextView timeTextView;

    public EventTimeViewHolder(View itemView) {
        super(itemView);

        timeTextView = (TextView) itemView.findViewById(R.id.event_time_text_view);
    }

    public void setTime(Date date) {
        timeTextView.setText(new SimpleDateFormat("HH:SS", Locale.US).format(date));
    }
}
