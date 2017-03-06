/**
 * Copyright 2017 Carl-Philipp Harmant
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

package fr.cph.stock.csv;

import com.opencsv.CSVReader;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.ShareValue;
import fr.cph.stock.entities.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is loading user data from a CSV file to DB. Not very stable, shouln't be use btw.
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
public class Csv {
	/** User **/
	private final User user;
	/** Reader **/
	private final BufferedReader reader;
	/** Account **/
	private final String account;

	/**
	 * Constructor
	 *
	 * @param reader
	 *            the reader
	 * @param user
	 *            the user
	 * @param account
	 *            the account
	 */
	public Csv(final BufferedReader reader, final User user, final String account) {
		this.reader = reader;
		this.user = user;
		this.account = account;
	}

	/**
	 *
	 * @return a list of share value
	 * @throws IOException
	 *             the io exception
	 * @throws ParseException
	 *             the parse exception
	 */
	public final List<ShareValue> getShareValueList() throws IOException, ParseException {
		final CSVReader csvReader = new CSVReader(reader, ';');
		final List<String[]> content = csvReader.readAll();
		final List<ShareValue> shareValues = new ArrayList<>();
		final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String[] row;
		final int userId = user.getId();
		for (final Object o : content) {
			row = (String[]) o;
			final ShareValue sv = new ShareValue();
			sv.setUserId(userId);
			sv.setBuy(0.0);
			sv.setDate(formatter.parse(row[0]));
			if (row[1] != null) {
				sv.setLiquidityMovement(format(row[1]));
			}
			if (row.length == 6) {
				sv.setMonthlyYield(format(row[5]));
			} else {
				sv.setMonthlyYield(0.0);
			}
			sv.setPortfolioValue(format(row[2]));
			sv.setSell(0.0);
			sv.setShareQuantity(format(row[3]));
			sv.setShareValue(format(row[4]));
			sv.setYield(0.0);
			sv.setTaxe(0.0);
			final Account acc = Account.builder().name(account).build();
			sv.setAccount(acc);
			shareValues.add(sv);
		}
		csvReader.close();
		return shareValues;
	}

	/**
	 * Format
	 *
	 * @param text
	 *            the text to format
	 * @return a Double
	 */
	private Double format(final String text) {
		Double d = 0d;
		if (text != null && !text.equals("")) {
			String temp = text.replaceAll(" ", "");
			temp = temp.replaceAll(",", ".");
			d = Double.parseDouble(temp);
		}
		return d;
	}
}
