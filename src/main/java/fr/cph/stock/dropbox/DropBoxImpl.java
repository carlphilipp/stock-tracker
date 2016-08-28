package fr.cph.stock.dropbox;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.WebAuthSession;
import fr.cph.stock.util.Util;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Log4j2
public class DropBoxImpl implements DropBox {

	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	private final DropboxAPI<WebAuthSession> dropBoxAPI;

	public DropBoxImpl() {
		final Properties prop = Util.getProperties();
		final String appKey = prop.getProperty("app_key");
		final String appSecret = prop.getProperty("app_secret");
		final String tokenKey = prop.getProperty("token_key");
		final String tokenValue = prop.getProperty("token_value");
		final AppKeyPair appKeys = new AppKeyPair(appKey, appSecret);
		final WebAuthSession session = new WebAuthSession(appKeys, Session.AccessType.APP_FOLDER, new AccessTokenPair(tokenKey, tokenValue));
		dropBoxAPI = new DropboxAPI<>(session);
	}

	/**
	 * Delete the file from last week.
	 *
	 * @param file the file to delete
	 * @throws ParseException   the parse exception
	 * @throws DropboxException the dropbox exception
	 */
	@Override
	public final void deleteOldFileIfNeeded(final File file) throws ParseException, DropboxException {
		final String date = file.getName().substring(0, file.getName().indexOf("-stock"));

		final Date d = SIMPLE_DATE_FORMAT.parse(date);
		final Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DAY_OF_MONTH, -7);
		final List<DropboxAPI.Entry> listEntry = dropBoxAPI.search("/", SIMPLE_DATE_FORMAT.format(cal.getTime()) + "-stock.tar.gz", 1, false);
		if (listEntry.size() != 0) {
			// This write a "Invalid cookie header" in the console. Not sure why.
			dropBoxAPI.delete(SIMPLE_DATE_FORMAT.format(cal.getTime()) + "-stock.tar.gz");
		}
	}

	/**
	 * Upload a file to dropbox
	 *
	 * @param file the file to upload
	 * @throws DropboxException the dropbox exception
	 * @throws IOException      the io exception
	 */
	@Override
	public final void uploadFile(final File file) throws DropboxException, IOException {
		try (FileInputStream inputStream = new FileInputStream(file)) {
			dropBoxAPI.putFile(file.getName(), inputStream, file.length(), null, null);
		}
	}
}
