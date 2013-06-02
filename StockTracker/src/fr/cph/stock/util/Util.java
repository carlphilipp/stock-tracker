package fr.cph.stock.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;

/**
 * This class is access only in a static way
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class Util {
	/**
	 * Constructor
	 */
	private Util() {
	}

	/** Logger **/
	private static final Logger log = Logger.getLogger(Util.class);

	/**
	 * Get the timezone difference with Paris
	 * 
	 * @param timezone
	 *            the timezone
	 * @return an hour
	 */
	public static int timeZoneDiff(TimeZone timezone) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
		int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		Calendar calendar2 = Calendar.getInstance(timezone);
		int hourOfDay2 = calendar2.get(Calendar.HOUR_OF_DAY);
		int dayOfMonth2 = calendar2.get(Calendar.DAY_OF_MONTH);

		int hourDifference = hourOfDay - hourOfDay2;
		int dayDifference = dayOfMonth - dayOfMonth2;
		if (dayDifference != 0) {
			hourDifference = hourDifference + 24;
		}
		return hourDifference;
	}

	/**
	 * Get date in timezone
	 * 
	 * @param date
	 *            the date
	 * @param timeZone
	 *            the timezone
	 * @return a Calendar
	 */
	public static Calendar getDateInTimeZone(Date date, TimeZone timeZone) {
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * Get current calendar in time zone
	 * 
	 * @param timeZone
	 *            the timezone
	 * @return the calendar
	 */
	public static Calendar getCurrentCalendarInTimeZone(TimeZone timeZone) {
		return Calendar.getInstance(timeZone);
	}

	/**
	 * Test if 2 calendar are in the same day
	 * 
	 * @param calendar1
	 *            the 1st calendar
	 * @param calendar2
	 *            the 2nd calendar
	 * @return a boolean
	 */
	public static boolean isSameDay(Calendar calendar1, Calendar calendar2) {
		boolean res = false;
		if (calendar1.getTimeZone() == calendar2.getTimeZone()) {
			if (calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
					&& calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)) {
				res = true;
			}
		}
		return res;
	}

	/**
	 * Get current date in specified format
	 * 
	 * @param format
	 *            the format to use
	 * @return the formatted date
	 */
	public static String getCurrentDateInFormat(String format) {
		Calendar cal = Util.getCurrentCalendarInTimeZone(TimeZone.getTimeZone("Europe/Paris"));
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(cal.getTime());
	}

	/**
	 * No idea what it is doing
	 * 
	 * @param hour1
	 * @param hour2
	 * @return
	 */
	public static int getRealHour(int hour1, int hour2) {
		int res = 0;
		int add = hour1 + hour2;
		if (add >= 24) {
			res = add - 24;
		} else {
			if (add < 0) {
				res = add + 24;
			} else {
				res = add;
			}
		}
		return res;
	}

	/**
	 * Get random colors
	 * 
	 * @param size
	 * @param colors
	 * @param res
	 * @return
	 */
	public static List<String> getRandomColors(int size, List<String> colors, List<String> res) {
		if (res.size() != size) {
			Random random = new Random();
			String color = colors.get(random.nextInt(colors.size()));
			if (!res.contains(color)) {
				res.add(color);
			}
			res = getRandomColors(size, colors, res);
		}
		return res;
	}

	/**
	 * Create tar.gz file
	 * 
	 * @param input
	 *            the input path
	 * @param output
	 *            the output path
	 * @throws IOException
	 */
	public static void createTarGz(String input, String output) throws IOException {
		FileOutputStream fOut = null;
		BufferedOutputStream bOut = null;
		GzipCompressorOutputStream gzOut = null;
		TarArchiveOutputStream tOut = null;
		try {
			fOut = new FileOutputStream(new File(output));
			bOut = new BufferedOutputStream(fOut);
			gzOut = new GzipCompressorOutputStream(bOut);
			tOut = new TarArchiveOutputStream(gzOut);

			File f = new File(input);
			TarArchiveEntry tarEntry = new TarArchiveEntry(f, input);
			tOut.putArchiveEntry(tarEntry);
			IOUtils.copy(new FileInputStream(f), tOut);
			tOut.closeArchiveEntry();
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			try {
				if (tOut != null)
					tOut.finish();
				if (tOut != null)
					tOut.close();
				if (gzOut != null)
					gzOut.close();
				if (bOut != null)
					bOut.close();
				if (tOut != null)
					fOut.close();
			} catch (IOException e) {
				throw new IOException(e);
			}
		}
	}

	/**
	 * Make a pause
	 * 
	 * @param timeInMs
	 *            the time to wait in ms
	 */
	public static void makeAPause(int timeInMs) {
		try {
			Thread.currentThread();
			Thread.sleep(timeInMs);
		} catch (InterruptedException e) {
			log.warn(e.getMessage());
		}
	}

	/**
	 * Reset hours, minutes, seconds and milliseconds
	 * 
	 * @param date
	 *            the date
	 * @return the date with reseted field
	 */
	public static Date resetHourMinSecMill(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 17);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date zeroedDate = cal.getTime();
		return zeroedDate;
	}

	/**
	 * Get properties from a property file
	 * 
	 * @param path
	 *            the path of the property file
	 * @return a properties file
	 */
	public static Properties getProperties(String path) {
		Properties prop = new Properties();
		try {
			prop.load(Util.class.getClassLoader().getResourceAsStream(path));
		} catch (IOException e) {
			log.error(e);
		}
		return prop;
	}
}
