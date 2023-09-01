package amai.org.conventions.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.VisibleForTesting;
import sff.org.conventions.BuildConfig;

public class Dates {
	public static final long MILLISECONDS_IN_MINUTE = java.util.concurrent.TimeUnit.MINUTES.toMillis(1);
	public static final long MILLISECONDS_IN_HOUR = java.util.concurrent.TimeUnit.HOURS.toMillis(1);
	public static final long MILLISECONDS_IN_DAY = java.util.concurrent.TimeUnit.DAYS.toMillis(1);

	public enum TimeUnit {
		DAY, HOUR, MINUTE, SECOND
	}

	private static final Locale LOCALE = new Locale("iw", "IL");
	private static final TimeZone CONVENTION_TIME_ZONE = TimeZone.getTimeZone("Asia/Jerusalem");
	private static final TimeZone DEVICE_TIME_ZONE = TimeZone.getDefault();
	private static final TimeZone LOCAL_TIME_ZONE = DEVICE_TIME_ZONE;
	private static Date appStartDate = new Date();
	private static Date initialDate = getInitialDate();

	private static Date getInitialDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Dates.getLocale());
		dateFormat.setTimeZone(LOCAL_TIME_ZONE);
		try {
			return dateFormat.parse("09.04.2023 16:10");
//			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@VisibleForTesting
	public static void setInitialDate(Date newInitialDate) {
		initialDate = newInitialDate;
	}

	public static Date now() {
		Date result;

		if (BuildConfig.DEBUG && initialDate != null) {
			// Used for testing
			// Fixed startup date
			result = new Date(System.currentTimeMillis() - appStartDate.getTime() + initialDate.getTime());
		} else {
			// Now
			result = new Date(System.currentTimeMillis());
		}


		if (!Dates.getLocalTimeZone().equals(TimeZone.getDefault())) {
			result = Dates.convertTimeZone(result, TimeZone.getDefault(), Dates.getLocalTimeZone());
		}
		return result;
	}

	public static Calendar toCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static boolean isSameDate(Calendar first, Calendar second) {
		return first.get(Calendar.DATE) == second.get(Calendar.DATE) &&
				first.get(Calendar.MONTH) == second.get(Calendar.MONTH) &&
				first.get(Calendar.YEAR) == second.get(Calendar.YEAR);
	}

	public static boolean isSameDate(Date first, Date second) {
		return isSameDate(toCalendar(first), toCalendar(second));
	}

	public static String toHumanReadableTimeDuration(long milliseconds) {
		return toHumanReadableTimeDuration(milliseconds, TimeUnit.MINUTE, true);
	}

	public static String toHumanReadableTimeDuration(long milliseconds, TimeUnit smallestUnit, boolean roundUp) {
		long x = milliseconds / 1000;
		int seconds = (int) (x % 60);
		x /= 60;
		int minutes = (int) (x % 60);
		x /= 60;
		int hours = (int) (x % 24);
		x /= 24;
		int days = (int) x;

		switch (smallestUnit) {
			case DAY:
				// Fallthrough
				hours = 0;
			case HOUR:
				minutes = 0;
				// Fallthrough
			case MINUTE:
				seconds = 0;
				// Fallthrough
			case SECOND:
				break;
		}

		if (roundUp && days == 0 && hours == 0 && minutes == 0 && seconds == 0) {
			switch (smallestUnit) {
				case DAY:
					days = 1;
					break;
				case HOUR:
					hours = 1;
					break;
				case MINUTE:
					minutes = 1;
					break;
				case SECOND:
					seconds = 1;
					break;
			}
		}

		return toHumanReadableTimeDuration(days, hours, minutes, seconds);
	}

	public static String toHumanReadableTimeDuration(int days, int hours, int minutes, int seconds) {
		List<String> parts = new ArrayList<>(3);
		if (days > 2) {
			parts.add(hours + " ימים");
		} else if (days == 2) {
			parts.add("יומיים");
		} else if (days == 1) {
			parts.add("יום אחד");
		}

		if (hours > 2) {
			parts.add(hours + " שעות");
		} else if (hours == 2) {
			parts.add("שעתיים");
		} else if (hours == 1) {
			parts.add("שעה");
		}

		if (minutes > 1) {
			parts.add(minutes + " דקות");
		} else if (minutes == 1) {
			parts.add("דקה");
		}

		if (seconds > 1) {
			parts.add(seconds + " שניות");
		} else if (seconds == 1) {
			parts.add("שנייה");
		}

		StringBuilder result = new StringBuilder();
		int size = parts.size();
		if (size == 1) {
			result.append(parts.get(0));
		} else {
			for (int i = 0; i < size; ++i) {
				result.append(parts.get(i));

				// Not last or before last
				if (i < size - 2) {
					result.append(", ");
				} else if (i == size - 2) { // before last
					result.append(" ו");
					if (Character.isDigit(parts.get(i + 1).charAt(0))) {
						result.append("-");
					}
				}
			}

		}

		return result.toString();
	}

	public static String formatDate(String pattern, Date date) {
		return new SimpleDateFormat(pattern, getLocale()).format(date);
	}

	public static String formatDateWithoutTime(Date date) {
		return formatDate("dd.MM.yyyy", date);
	}

	public static String formatHoursAndMinutes(Date date) {
		return formatDate("HH:mm", date);
	}

	public static String formatDay(Date date) {
		return formatDate("EEEE", date);
	}

	public static String formatDateAndTime(Date date) {
		return formatDate("dd.MM.yyyy HH:mm", date);
	}

	public static Locale getLocale() {
		return LOCALE;
	}

	public static TimeZone getConventionTimeZone() {
		return CONVENTION_TIME_ZONE;
	}

	public static TimeZone getLocalTimeZone() {
		return LOCAL_TIME_ZONE;
	}

	public static Date utcToLocalTime(Date utcTime) {
		long timeInMillis = utcTime.getTime();
		return new Date(timeInMillis + Dates.getLocalTimeZone().getOffset(timeInMillis));
	}

	public static Date localToUTCTime(Date conventionTime) {
		long timeInMillis = conventionTime.getTime();
		return new Date(timeInMillis - Dates.getLocalTimeZone().getOffset(timeInMillis));
	}

	public static Date localToConventionTime(Date localTime) {
		return convertTimeZone(localTime, Dates.getLocalTimeZone(), Dates.getConventionTimeZone());
	}

	public static Date conventionToLocalTime(Date conventionTime) {
		return convertTimeZone(conventionTime, Dates.getConventionTimeZone(), Dates.getLocalTimeZone());
	}

	public static Date localToDeviceTime(Date localTime) {
		return convertTimeZone(localTime, Dates.getLocalTimeZone(), DEVICE_TIME_ZONE);
	}

	public static Date convertTimeZone(Date time, TimeZone from, TimeZone to) {
		if (from.equals(to)) {
			return time;
		}
		long timeInMillis = time.getTime();
		return new Date(timeInMillis -
			from.getOffset(timeInMillis) +  // Convert from "from" to UTC
			to.getOffset(timeInMillis) // Convert from UTC to "to"
		);
	}

	public static Calendar createDate(int year, int month, int day) {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(year, month, day);
		return date;
	}
}
