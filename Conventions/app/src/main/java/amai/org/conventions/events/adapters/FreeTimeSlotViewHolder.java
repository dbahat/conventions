package amai.org.conventions.events.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import amai.org.conventions.R;
import amai.org.conventions.events.activities.EventsTimeSlot;
import amai.org.conventions.utils.Dates;

public class FreeTimeSlotViewHolder extends RecyclerView.ViewHolder {
	private TextView textView;

	public FreeTimeSlotViewHolder(View view) {
		super(view);
		textView = (TextView) view.findViewById(R.id.small_text);
	}

	public void setModel(EventsTimeSlot slot) {
		textView.setText(textView.getContext().getString(R.string.free_time,
				Dates.toHumanReadableTimeDuration(slot.getEndTime().getTime() - slot.getStartTime().getTime())));
	}
}
