package fr.cph.stock.dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.SearchMatch;
import fr.cph.stock.config.AppProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Log4j2
public class DropBoxImpl implements DropBox {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private final DbxClientV2 client;

	@Autowired
	public DropBoxImpl(final AppProperties appProperties) {
		final DbxRequestConfig config = new DbxRequestConfig(appProperties.getDropbox().getClientId());
		client = new DbxClientV2(config, appProperties.getDropbox().getClientId());
	}

	/**
	 * Delete the file from last week.
	 *
	 * @param file the file to delete
	 * @throws ParseException the parse exception
	 * @throws DbxException   the dropbox exception
	 */
	@Override
	public final void deleteOldFileIfNeeded(final File file) throws DbxException {
		final String date = calculateNewDateFromFileName(file);

		final List<SearchMatch> searchMatches = client.files().search("", date + "-stock.tar.gz").getMatches();
		searchMatches.stream().findAny().ifPresent(searchMatch -> {
			try {
				client.files().delete("/" + date + "-stock.tar.gz");
			} catch (final DbxException e) {
				log.error("Error while deleting DropBox file", e);
			}
		});
	}

	/**
	 * Upload a file to dropbox
	 *
	 * @param file the file to upload
	 * @throws DbxException the dropbox exception
	 * @throws IOException  the io exception
	 */
	@Override
	public final void uploadFile(final File file) throws IOException, DbxException {
		try (final InputStream in = new FileInputStream(file.getName())) {
			client.files().uploadBuilder("/" + file.getName()).uploadAndFinish(in);
		}
	}

	String calculateNewDateFromFileName(final File file) {
		final String date = file.getName().substring(0, file.getName().indexOf("-stock"));
		final LocalDate newLocalDate = LocalDate.parse(date, FORMATTER).minusDays(7);
		return newLocalDate.format(FORMATTER);
	}
}
