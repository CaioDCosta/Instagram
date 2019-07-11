package com.example.instagram.utils;

import android.text.format.DateUtils;

import java.util.Date;

public class Time {
	public static String getRelativeTimeAgo(Date date) {
		String relativeDate = "";
		long dateMillis = date.getTime() - 1000; // Subtract to avoid new comments getting "In 0 sec"
		relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
				System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();

		return relativeDate.replace("ago", "");
	}
}
