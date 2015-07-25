package amai.org.conventions.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import amai.org.conventions.model.Convention;

public class Dates {

	public enum TimeUnit {
        HOUR, MINUTE, SECOND
    }

	private static Locale LOCALE = new Locale("iw", "IL");
    private static Date appStartDate = new Date();
    private static Date initialDate = getInitialDate();

    private static Date getInitialDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Dates.getLocale());
        try {
            return dateFormat.parse("20.08.2015 09:14");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date now() {
	    // Now
	    return new Date(System.currentTimeMillis());

	    // Fixed startup date
//        return new Date(System.currentTimeMillis() - appStartDate.getTime() + initialDate.getTime());

	    // Current time at the convention's date
//	    Calendar currDate = Calendar.getInstance();
//	    setConventionDate(currDate);
//	    return currDate.getTime();
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
        if (hours > 1) {
            parts.add(hours + " שעות");
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

    public static Date parseHourAndMinute(String date) {
        try {
            Date hourAndMinute = new SimpleDateFormat("HH:mm:ss", getLocale()).parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(hourAndMinute);
	        setConventionDate(calendar);
            return calendar.getTime();
        } catch (ParseException e) {
            return new Date();
        }
    }

	public static void setConventionDate(Calendar calendar) {
            calendar.set(Convention.getInstance().getDate().get(Calendar.YEAR),
                    Convention.getInstance().getDate().get(Calendar.MONTH),
                    Convention.getInstance().getDate().get(Calendar.DAY_OF_MONTH));
	}

	public static Locale getLocale() {
		return LOCALE;
	}
}
