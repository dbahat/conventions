package amai.org.conventions.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import amai.org.conventions.model.conventions.Convention;

public class Dates {
	public static final long MILLISECONDS_IN_MINUTE = java.util.concurrent.TimeUnit.MINUTES.toMillis(1);
	public static final long MILLISECONDS_IN_HOUR = java.util.concurrent.TimeUnit.HOURS.toMillis(1);
	public static final long MILLISECONDS_IN_DAY = java.util.concurrent.TimeUnit.DAYS.toMillis(1);

	public enum TimeUnit {
        HOUR, MINUTE, SECOND
    }

	private static final Locale LOCALE = new Locale("iw", "IL");
    private static Date appStartDate = new Date();
    private static Date initialDate = getInitialDate();

    private static Date getInitialDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Dates.getLocale());
        try {
            return dateFormat.parse("24.03.2016 17:10");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date now() {
	    // Now
	    return new Date(System.currentTimeMillis());

	    // Used for testing
	    // Fixed startup date
//        return new Date(System.currentTimeMillis() - appStartDate.getTime() + initialDate.getTime());

	    // Current time at the convention's date
//	    Calendar currDate = Calendar.getInstance();
//	    setConventionDate(currDate);
//	    return currDate.getTime();
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

    public static String toHumanReadableTimeDuration(long milliseconds) {
        return toHumanReadableTimeDuration(milliseconds, TimeUnit.MINUTE);
    }

    public static String toHumanReadableTimeDuration(long milliseconds, TimeUnit smallestUnit) {
        long x = milliseconds / 1000;
        int seconds = (int) (x % 60);
        x /= 60;
        int minutes = (int) (x % 60);
        x /= 60;
        int hours = (int) (x % 24);

        switch (smallestUnit) {
            case HOUR:
                minutes = 0;
                // Fallthrough
            case MINUTE:
                seconds = 0;
                // Fallthrough
            case SECOND:
                break;
        }

        return toHumanReadableTimeDuration(hours, minutes, seconds);
    }

    public static String toHumanReadableTimeDuration(int hours, int minutes, int seconds) {
        List<String> parts = new ArrayList<>(3);
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

    public static String formatDateWithoutTime(Date date) {
        return new SimpleDateFormat("dd.MM.yyyy", getLocale()).format(date);
    }

    public static String formatHoursAndMinutes(Date date) {
        return new SimpleDateFormat("HH:mm", getLocale()).format(date);
    }

	public static String formatDateAndTime(Date date) {
		return new SimpleDateFormat("dd.MM.yyyy HH:mm", getLocale()).format(date);
	}

	public static Locale getLocale() {
		return LOCALE;
	}

	public static Calendar createDate(int year, int month, int day) {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(year, month, day);
		return date;
	}
}
