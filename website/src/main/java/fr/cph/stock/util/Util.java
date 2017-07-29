package fr.cph.stock.util;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class is access only in a static way
 *
 * @author Carl-Philipp Harmant
 */
@Log4j2
public enum Util {
	;

	private static final Random RANDOM = new Random();

	/**
	 * Get the timezone difference with Paris
	 *
	 * @param timezone the timezone
	 * @return an hour
	 */
	public static int timeZoneDiff(final TimeZone timezone) {
		final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
		final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
		final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		final Calendar calendar2 = Calendar.getInstance(timezone);
		final int hourOfDay2 = calendar2.get(Calendar.HOUR_OF_DAY);
		final int dayOfMonth2 = calendar2.get(Calendar.DAY_OF_MONTH);

		int hourDifference = hourOfDay - hourOfDay2;
		final int dayDifference = dayOfMonth - dayOfMonth2;
		final int hourInDay = 24;
		if (dayDifference != 0) {
			hourDifference = hourDifference + hourInDay;
		}
		return hourDifference;
	}

	/**
	 * Get date in timezone
	 *
	 * @param date     the date
	 * @param timeZone the timezone
	 * @return a Calendar
	 */
	public static Calendar getDateInTimeZone(final Date date, final TimeZone timeZone) {
		final Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * Get current calendar in time zone
	 *
	 * @param timeZone the timezone
	 * @return the calendar
	 */
	public static Calendar getCurrentCalendarInTimeZone(final TimeZone timeZone) {
		return Calendar.getInstance(timeZone);
	}

	/**
	 * Test if 2 calendar are in the same day
	 *
	 * @param calendar1 the 1st calendar
	 * @param calendar2 the 2nd calendar
	 * @return a boolean
	 */
	public static boolean isSameDay(final Calendar calendar1, final Calendar calendar2) {
		boolean res = false;
		if (calendar1.getTimeZone() == calendar2.getTimeZone()
			&& calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
			&& calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)) {
			res = true;
		}
		return res;
	}

	/**
	 * Get current date in specified format
	 *
	 * @param format the format to use
	 * @return the formatted date
	 */
	public static String getCurrentDateInFormat(final String format) {
		final Calendar cal = Util.getCurrentCalendarInTimeZone(TimeZone.getTimeZone("America/Chicago"));
		final DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(cal.getTime());
	}

	/**
	 * Not sure what it is doing
	 *
	 * @param hour1 the 1st hour
	 * @param hour2 the second hour
	 * @return an int
	 */
	public static int getRealHour(final int hour1, final int hour2) {
		final int hourInDay = 24;
		int res;
		int add = hour1 + hour2;
		if (add >= hourInDay) {
			res = add - hourInDay;
		} else {
			if (add < 0) {
				res = add + hourInDay;
			} else {
				res = add;
			}
		}
		return res;
	}

	/**
	 * Get RANDOM colors
	 *
	 * @param size   the size
	 * @param colors the list of color
	 * @return a list of string
	 */
	public static List<String> getRandomColors(final int size, final List<String> colors) {
		final Set<String> res = new HashSet<>();
		while (res.size() != size) {
			final String color = colors.get(RANDOM.nextInt(colors.size()));
			res.add(color);
		}
		return new ArrayList<>(res);
	}

	/**
	 * Create tar.gz file
	 *
	 * @param input  the input path
	 * @param output the output path
	 * @throws IOException the exception
	 */
	public static void createTarGz(final String input, final String output) throws IOException {
		try (final FileOutputStream fOut = new FileOutputStream(new File(output));
			 final BufferedOutputStream bOut = new BufferedOutputStream(fOut);
			 final GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(bOut);
			 final TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {
			final File f = new File(input);
			final TarArchiveEntry tarEntry = new TarArchiveEntry(f, input);
			tOut.putArchiveEntry(tarEntry);
			IOUtils.copy(new FileInputStream(f), tOut);
			tOut.closeArchiveEntry();
		} catch (final IOException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Make a pause
	 *
	 * @param timeInMs the time to wait in ms
	 */
	public static void makeAPause(final int timeInMs) {
		try {
			Thread.currentThread();
			Thread.sleep(timeInMs);
		} catch (final InterruptedException e) {
			log.warn(e.getMessage(), e);
		}
	}

	/**
	 * Reset hours, minutes, seconds and milliseconds
	 *
	 * @param date the date
	 * @return the date with reseted field
	 */
	public static Date resetHourMinSecMill(final Date date) {
		final int hourOfDay = 17;
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
}
