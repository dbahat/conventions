package amai.org.conventions.events.adapters;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import amai.org.conventions.utils.Dates;

public abstract class DayFragmentAdapter extends FragmentStatePagerAdapter {
	private final Calendar startDate;
	private final int days;

	public DayFragmentAdapter(FragmentManager fm, Calendar startDate, int days) {
		super(fm);
		this.startDate = startDate;
		this.days = days;
	}

	protected Calendar getDate(int position) {
		Calendar date = Calendar.getInstance();
		date.setTime(startDate.getTime());
		date.add(Calendar.DATE, position);
		return date;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE (dd.MM)", Dates.getLocale());
		return sdf.format(getDate(position).getTime());
	}

	@Override
	public int getCount() {
		return days;
	}
}
