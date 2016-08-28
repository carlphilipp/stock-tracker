/**
 * Copyright 2016 Carl-Philipp Harmant
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

import com.dropbox.core.DbxException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * This class connects to dropbox, upload and delete files. It is used to save DB into dropbox. It deletes last week dump and
 * upload the new one
 *
 * @author Carl-Philipp Harmant
 */
public interface DropBox {

	void deleteOldFileIfNeeded(final File file) throws ParseException, DbxException;

	void uploadFile(final File file) throws IOException, DbxException;
}
