package amai.org.conventions.events.holders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import amai.org.conventions.utils.Dates;

public class TimeViewHolder extends RecyclerView.ViewHolder {
	private TextView timeTextView;
	private Calendar date;

	public TimeViewHolder(View itemView, int timeTextViewResourceId) {
		super(itemView);

		timeTextView = (TextView) itemView.findViewById(timeTextViewResourceId);
	}

	public void setTime(Date date, String timeFormat) {
		timeTextView.setText(new SimpleDateFormat(timeFormat, Dates.getLocale()).format(date));

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		this.date = calendar;
	}

	public Calendar getTime() {
		return date;
	}

	public void setTextColor(int color) {
		timeTextView.setTextColor(color);
	}
}
