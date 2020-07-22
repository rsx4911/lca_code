package com.greendelta.collaboration.util;

import java.util.Calendar;
import java.util.Date;

public class Dates {

	public static Date getLatest(Date... dates) {
		if (dates == null)
			return null;
		Date latest = null;
		for (Date date : dates) {
			if (date != null && latest != null && isBefore(date, latest))
				continue;
			latest = date;
		}
		return latest;
	}

	public static boolean isBefore(Date toCompare, Date toCompareTo) {
		return isBefore(toCompare, toCompareTo, true);
	}

	private static boolean isBefore(Date toCompare, Date toCompareTo, boolean considerTime) {
		Calendar calendar1 = toCalendar(toCompare, considerTime);
		Calendar calendar2 = toCalendar(toCompareTo, considerTime);
		return calendar1.before(calendar2);
	}

	public static Calendar toCalendar(Date date, boolean considerTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (!considerTime) {
			removeTimeInformation(calendar);
		}
		return calendar;
	}

	public static void removeTimeInformation(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}
}
