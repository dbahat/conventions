package amai.org.conventions.events.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.activities.EventsTimeSlot;
import amai.org.conventions.networking.AmaiModelConverter;
import amai.org.conventions.utils.Dates;

public class FreeTimeSlotViewHolder extends RecyclerView.ViewHolder {
	private TextView textView;

	public FreeTimeSlotViewHolder(View view) {
		super(view);
		textView = (TextView) view.findViewById(R.id.small_text);
	}

	public void setModel(EventsTimeSlot slot) {
		Date startTime = slot.getStartTime();
		Date endTime = slot.getEndTime();
		textView.setText(textView.getContext().getString(R.string.free_time,
				Dates.toHumanReadableTimeDuration(endTime.getTime() - startTime.getTime())));

		// Set color according to started/during/passed
		int color;
		if (!startTime.before(Dates.now())) {
			color = ThemeAttributes.getColor(itemView.getContext(), R.attr.eventTypeNotStartedColor);
		} else if (endTime.before(Dates.now())) {
			color = ThemeAttributes.getColor(itemView.getContext(), R.attr.eventTypeEndedColor);
		} else {
			color = ThemeAttributes.getColor(itemView.getContext(), R.attr.eventTypeCurrentColor);
		}
		if (color == AmaiModelConverter.NO_COLOR) {
			color = ThemeAttributes.getColor(itemView.getContext(), R.attr.eventTimeHeaderDefaultTextColor);
		}
		textView.setTextColor(color);
	}
}
