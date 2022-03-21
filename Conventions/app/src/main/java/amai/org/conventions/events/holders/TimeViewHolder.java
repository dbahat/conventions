package amai.org.conventions.events.holders;

import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import amai.org.conventions.utils.Dates;
import androidx.recyclerview.widget.RecyclerView;

public class TimeViewHolder extends RecyclerView.ViewHolder {
	private TextView timeTextView;
	private Calendar date;

	public TimeViewHolder(View itemView, int timeTextViewResourceId) {
		super(itemView);

		timeTextView = (TextView) itemView.findViewById(timeTextViewResourceId);
	}

	public void setTime(Date date, String timeFormat) {
		timeTextView.setText(Dates.formatDate(timeFormat, date));

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
