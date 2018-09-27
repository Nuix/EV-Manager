package com.nuix.evmanager;

import java.text.NumberFormat;
import java.util.Locale;

/***
 * Provides some convenience methods for common formatting tasks.
 * @author Jason Wells
 *
 */
public class FormatHelpers {
	private static final int SECOND = 1;
	private static final int MINUTE = SECOND * 60;
	private static final int HOUR = MINUTE * 60;
	private static final int DAY = 24 * HOUR;
	
	/***
	 * Formats a value representing an amount of seconds to a human friendly string.
	 * For example:
	 * 82,800 seconds (23 hours) becomes "00:23:00"
	 * 90,000 seconds (25 hours) becomes "1 Day 01:00:00"
	 * 176,400 seconds (49 hours) becomes "2 Days 01:00:00"
	 * @param offsetSeconds Amount of seconds
	 * @return A strings with a human friendly representation of the amount of time
	 */
	public static String secondsToElapsedString(long offsetSeconds){
		long seconds = offsetSeconds;
		
		long days = seconds / DAY;
		seconds -= days * DAY;
		
		long hours = seconds / HOUR;
		seconds -= hours * HOUR;
		
		long minutes = seconds / MINUTE;
		seconds -= minutes * MINUTE;
		
		if(days > 0){
			if(days > 1){
				return String.format("%d Days %02d:%02d:%02d",days,hours,minutes,seconds);	
			} else {
				return String.format("%d Day %02d:%02d:%02d",days,hours,minutes,seconds);
			}
		} else {
			return String.format("%02d:%02d:%02d",hours,minutes,seconds);	
		}
	}
	
	/***
	 * Convenience methods for formatting an integer using US locale into a string
	 * @param number The number to format into a string
	 * @return The number formatted into a string using US locale
	 */
	public static String formatNumber(int number){
		return NumberFormat.getNumberInstance(Locale.US).format(number);
	}
	
	/***
	 * Convenience methods for formatting a long integer using US locale into a string
	 * @param number The number to format into a string
	 * @return The number formatted into a string using US locale
	 */
	public static String formatNumber(long number){
		return NumberFormat.getNumberInstance(Locale.US).format(number);
	}
	
	/***
	 * Convenience methods for formatting a double using US locale into a string
	 * @param number The number to format into a string
	 * @return The number formatted into a string using US locale
	 */
	public static String formatNumber(double number){
		return NumberFormat.getNumberInstance(Locale.US).format(number);
	}
}
