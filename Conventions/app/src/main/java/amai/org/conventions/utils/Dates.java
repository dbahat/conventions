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

    private static final String SFF_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:SS";
    
	public static final long MILLISECONDS_IN_MINUTE = 60 * 1000;

	public enum TimeUnit {
        HOUR, MINUTE, SECOND
    }

	private static final Locale LOCALE = new Locale("iw", "IL");

    public static Date parseSffFormat(String sffFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SFF_DATE_FORMAT, Dates.getLocale());
        try {
            return dateFormat.parse(sffFormat);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date now() {
	    return new Date(System.currentTimeMillis());
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

    public static Date parseConventionDateHourAndMinute(String date) {
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

	private static void setConventionDate(Calendar calendar) {
		Calendar conventionDate = Convention.getInstance().getDate();
		calendar.set(conventionDate.get(Calendar.YEAR),
                    conventionDate.get(Calendar.MONTH),
                    conventionDate.get(Calendar.DAY_OF_MONTH));
	}

	public static Locale getLocale() {
		return LOCALE;
	}
}
