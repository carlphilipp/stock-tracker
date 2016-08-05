/**
 * Copyright 2013 Carl-Philipp Harmant
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.cph.stock.dropbox;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import fr.cph.stock.util.Util;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * This class connects to dropbox, upload and delete files. It is used to save DB into dropbox. It deletes last week dump and
 * upload the new one
 *
 * @author Carl-Philipp Harmant
 */
public class DropBox {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(DropBox.class);
    private DropboxAPI<WebAuthSession> dropBoxAPI;

    /**
     * Constructor
     */
    public DropBox() {
        final Properties prop = Util.getProperties("app.properties");
        final String appKey = prop.getProperty("app_key");
        final String appSecret = prop.getProperty("app_secret");
        final String tokenKey = prop.getProperty("token_key");
        final String tokenValue = prop.getProperty("token_value");
        final AppKeyPair appKeys = new AppKeyPair(appKey, appSecret);
        final WebAuthSession session = new WebAuthSession(appKeys, AccessType.APP_FOLDER, new AccessTokenPair(tokenKey, tokenValue));
        dropBoxAPI = new DropboxAPI<>(session);
    }

    /**
     * Delete the file from last week.
     *
     * @param file the file to delete
     * @throws ParseException   the parse exception
     * @throws DropboxException the dropbox exception
     */
    public final void deleteOldFileIfNeeded(final File file) throws ParseException, DropboxException {
        final String date = file.getName().substring(0, file.getName().indexOf("-stock"));
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        final Date d = sdf.parse(date);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        final List<Entry> listEntry = dropBoxAPI.search("/", sdf.format(cal.getTime()) + "-stock.tar.gz", 1, false);
        if (listEntry.size() != 0) {
            // This write a "Invalid cookie header" in the console. Not sure why.
            dropBoxAPI.delete(sdf.format(cal.getTime()) + "-stock.tar.gz");
        }
    }

    /**
     * Upload a file to dropbox
     *
     * @param file the file to upload
     * @throws DropboxException the dropbox exception
     * @throws IOException      the io exception
     */
    public final void uploadFile(final File file) throws DropboxException, IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            dropBoxAPI.putFile(file.getName(), inputStream, file.length(), null, null);
        }
    }

    // public static void main(String args[]) throws DropboxException, MalformedURLException, IOException, URISyntaxException {
    // AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
    // WebAuthSession session = new WebAuthSession(appKeys, ACCESS_TYPE);
    // WebAuthInfo authInfo = session.getAuthInfo();
    //
    // RequestTokenPair pair = authInfo.requestTokenPair;
    // String url = authInfo.url;
    //
    // Desktop.getDesktop().browse(new URL(url).toURI());
    // JOptionPane.showMessageDialog(null, "Press ok to continue once you have authenticated.");
    // session.retrieveWebAccessToken(pair);
    //
    // AccessTokenPair tokens = session.getAccessTokenPair();
    // System.out.println("Use this token pair in future so you don't have to re-authenticate each time:");
    // System.out.println("Key token: " + tokens.key);
    // System.out.println("Secret token: " + tokens.secret);
    //
    // dropBoxAPI = new DropboxAPI<>(session);
    //
    // System.out.println();
    // System.out.print("Uploading file...");
    // String fileContents = "Hello World!";
    // ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContents.getBytes());
    // Entry newEntry = dropBoxAPI.putFile("/testing.txt", inputStream, fileContents.length(), null, null);
    // System.out.println("Done. \nRevision of file: " + newEntry.rev);
    //
    // }
}
