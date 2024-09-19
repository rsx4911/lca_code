package com.greendelta.collaboration.util;

import java.util.Calendar;
import java.util.Date;

import org.openlca.jsonld.Json;

public class Dates {

	public static Date getLatest(Date... dates) {
		if (dates == null)
			return null;
		Date latest = null;
		for (Date date : dates) {
			if (date == null || (latest != null && isBefore(date, latest)))
				continue;
			latest = date;
		}
		return latest;
	}

	public static boolean isBefore(Date toCompare, Date toCompareTo) {
		return isBefore(toCompare, toCompareTo, true);
	}

	private static boolean isBefore(Date toCompare, Date toCompareTo, boolean considerTime) {
		var calendar1 = toCalendar(toCompare, considerTime);
		var calendar2 = toCalendar(toCompareTo, considerTime);
		return calendar1.before(calendar2);
	}

	public static Calendar toCalendar(Date date, boolean considerTime) {
		var calendar = Calendar.getInstance();
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

	public static Date fromString(String date) {
		if (date == null || date.isEmpty())
			return null;
		return Json.parseDate(date);
	}

	public static long getTime(String date) {
		var d = fromString(date);
		return d == null ? 0 : d.getTime();
	}

}
