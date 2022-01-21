package amai.org.conventions.events.adapters;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import amai.org.conventions.utils.Dates;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public abstract class DayFragmentAdapter extends FragmentStatePagerAdapter {
	private final Calendar[] eventDates;

	public DayFragmentAdapter(FragmentManager fm, Calendar[] eventDates) {
		super(fm);
		this.eventDates = eventDates;
	}

	protected Calendar getDate(int position) {
		Calendar date = Calendar.getInstance();
		if (position >= eventDates.length) {
			position = eventDates.length - 1;
		}
		date.setTime(eventDates[position].getTime());
		return date;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE (dd.MM)", Dates.getLocale());
		return sdf.format(getDate(position).getTime());
	}

	@Override
	public int getCount() {
		return eventDates.length;
	}

	public int getItemToDisplayForDate(Calendar date) {
		// If there are events in this date, return its position.
		// If there are events after this date, return the date of the first day with events after the date.
		// Otherwise, return the first date's position.
		for (int i = 0; i < eventDates.length; ++i) {
			if (Dates.isSameDate(date, eventDates[i])) {
				return i;
			} else if (eventDates[i].after(date)) {
				return i;
			}
		}

		return 0;
	}
}
