package amai.org.conventions.events.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.activities.EventsTimeSlot;
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
		int[] attributes = new int[1];
		if (!startTime.before(Dates.now())) {
			attributes[0] = R.attr.state_event_not_started;
		} else if (endTime.before(Dates.now())) {
			attributes[0] = R.attr.state_event_ended;
		} else {
			attributes[0] = R.attr.state_event_current;
		}
		int color = ThemeAttributes.getColorFromStateList(itemView.getContext(), R.attr.freeTimeTextColor, attributes);
		Drawable background = ThemeAttributes.getDrawable(itemView.getContext(), R.attr.freeTimeBackground);
		textView.setTextColor(color);
		itemView.setBackground(background);
	}
}
