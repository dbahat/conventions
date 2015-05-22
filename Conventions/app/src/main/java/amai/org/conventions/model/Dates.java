package amai.org.conventions.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Dates {
	public enum TimeUnit {
		HOUR, MINUTE, SECOND;
	}

	private static Date appStartDate = new Date();
	private static Date initialDate = getInitialDate();

    private static Date getInitialDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            return dateFormat.parse("05.03.2015 14:47");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

	public static Date now() {
        // TODO this is a mock for testing purpose. Change to new Date() when it's the real app.
		return new Date(System.currentTimeMillis() - appStartDate.getTime() + initialDate.getTime());
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

	public static String formatHoursAndMinutes(Date date) {
		return new SimpleDateFormat("HH:mm").format(date);
	}
}
