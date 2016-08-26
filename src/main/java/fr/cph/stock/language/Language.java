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

package fr.cph.stock.language;

import fr.cph.stock.exception.LanguageException;
import lombok.extern.log4j.Log4j2;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extract from XML files and put in a map, all the translation needed.
 *
 * @author Carl-Philipp Harmant
 */
@Log4j2
public class Language {

	/**
	 * Path of the XML file
	 **/
	private String path;
	private XMLRetriever xml;
	private Map<String, String> map;
	private static FileSystem system = FileSystems.getDefault();
	private static final String BASE = "/root";
	private static final String CONSTANT = "/constant";
	private static final String MENU = "/menu/title";
	private static final String PORTFOLIO = "/portfolio";
	private static final String PORTFOLIO_HIDDEN = "/portfolio_hidden";
	private static final String HISTORY = "/history";
	private static final String HISTORY_HIDDEN = "/history_hidden";
	private static final String ACCOUNTS = "/accounts";
	private static final String ACCOUNTS_HIDDEN = "/accounts_hidden";
	private static final String LIST = "/list";
	private static final String LIST_HIDDEN = "/list_hidden";
	private static final String CHARTS = "/charts";
	private static final String CURRENCIES = "/currencies";
	private static final String CURRENCIES_HIDDEN = "/currencies_hidden";
	private static final String OPTIONS = "/options";
	private static final String OPTIONS_HIDDEN = "/options_hidden";

	/**
	 * Constructor
	 *
	 * @param p the path of the xml file
	 * @throws LanguageException the language exception
	 */
	protected Language(final String path) {
		this.path = path;
		try {
			this.xml = new XMLRetriever(path);
		} catch (final IOException | DocumentException e) {
			log.error(e.getMessage(), e);
			throw new LanguageException(e.getMessage(), e);
		}
		this.map = new HashMap<>();
	}

	/**
	 * Get the language name (from the xml name file)
	 *
	 * @return the language name
	 */
	public final String getLanguageName() {
		return path.substring(path.indexOf(system.getSeparator() + "xml" + system.getSeparator()) + 5, path.indexOf(".xml"));
	}

	/**
	 * Get the language map
	 *
	 * @return a map
	 */
	public final Map<String, String> getLanguage() {
		Node menuNode = xml.getNode(BASE + MENU);
		@SuppressWarnings("unchecked")
		List<Node> menuNodes = (List<Node>) xml.getListNode(BASE + MENU);
		for (Node node : menuNodes) {
			String name = node.valueOf("@name");
			map.put(menuNode.getName().toUpperCase() + "_" + name.toUpperCase(), node.getStringValue());
		}
		Node node = xml.getNode(BASE + PORTFOLIO);
		List<String> nodes = new ArrayList<>();
		Node node2;
		nodes.add(BASE + PORTFOLIO + "/title");
		nodes.add(BASE + PORTFOLIO + "/review/liquidity");
		nodes.add(BASE + PORTFOLIO + "/review/yieldYear");
		nodes.add(BASE + PORTFOLIO + "/review/shareValue");
		nodes.add(BASE + PORTFOLIO + "/add");
		nodes.add(BASE + PORTFOLIO + "/equities/company");
		nodes.add(BASE + PORTFOLIO + "/equities/quantity");
		nodes.add(BASE + PORTFOLIO + "/equities/unitCostPrice");
		nodes.add(BASE + PORTFOLIO + "/equities/quote");
		nodes.add(BASE + PORTFOLIO + "/equities/currency");
		nodes.add(BASE + PORTFOLIO + "/equities/parities");
		nodes.add(BASE + PORTFOLIO + "/equities/value");
		nodes.add(BASE + PORTFOLIO + "/equities/percentTotal");
		nodes.add(BASE + PORTFOLIO + "/equities/yieldTtm");
		nodes.add(BASE + PORTFOLIO + "/equities/yieldPerUnitCostPrice");
		nodes.add(BASE + PORTFOLIO + "/equities/valueGained");
		nodes.add(BASE + PORTFOLIO + "/equities/stopLoss");
		nodes.add(BASE + PORTFOLIO + "/equities/objective");
		nodes.add(BASE + PORTFOLIO + "/equities/info");
		nodes.add(BASE + PORTFOLIO + "/equities/info");
		nodes.add(BASE + PORTFOLIO + "/equities/modify");
		nodes.add(BASE + PORTFOLIO + "/chartTitle");
		nodes.add(BASE + PORTFOLIO + "/chartTitleValue");
		nodes.add(BASE + PORTFOLIO + "/chart/all");
		nodes.add(BASE + PORTFOLIO + "/chart/fiveYears");
		nodes.add(BASE + PORTFOLIO + "/chart/twoYears");
		nodes.add(BASE + PORTFOLIO + "/chart/oneYear");
		nodes.add(BASE + PORTFOLIO + "/chart/sixMonths");
		nodes.add(BASE + PORTFOLIO + "/chart/threeMonths");
		nodes.add(BASE + PORTFOLIO + "/chart/oneMonth");
		nodes.add(BASE + PORTFOLIO + "/chart/oneWeek");
		for (String n : nodes) {
			node2 = xml.getNode(n);
			addToMap(map, node, node2);
		}
		node = xml.getNode(BASE + CONSTANT);
		nodes = new ArrayList<>();
		nodes.add(BASE + CONSTANT + "/added");
		nodes.add(BASE + CONSTANT + "/updated");
		nodes.add(BASE + CONSTANT + "/modified");
		nodes.add(BASE + CONSTANT + "/deleted");
		for (String n : nodes) {
			node2 = xml.getNode(n);
			addToMap(map, node, node2);
		}
		node = xml.getNode(BASE + PORTFOLIO_HIDDEN);
		nodes = new ArrayList<>();
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/refresh");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/add");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/updateCurrencies");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/yahooId");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/quantity");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/unitCostPrice");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/modifyEquity");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/customizeName");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/customizeName");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/customizeSector");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/customizeIndustry");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/customizeMarketCap");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/defautParity");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/personalParity");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/yahooYield");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/personalYield");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/stopLoss");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/objective");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/modify");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/or");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/delete");
		nodes.add(BASE + PORTFOLIO_HIDDEN + "/deleteConfirm");
		for (String n : nodes) {
			node2 = xml.getNode(n);
			addToMap(map, node, node2);
		}
		node = xml.getNode(BASE + HISTORY);
		nodes = new ArrayList<>();
		nodes.add(BASE + HISTORY + "/title");
		nodes.add(BASE + HISTORY + "/update");
		nodes.add(BASE + HISTORY + "/date");
		nodes.add(BASE + HISTORY + "/account");
		nodes.add(BASE + HISTORY + "/liquidityMovement");
		nodes.add(BASE + HISTORY + "/dividends");
		nodes.add(BASE + HISTORY + "/buy");
		nodes.add(BASE + HISTORY + "/sell");
		nodes.add(BASE + HISTORY + "/taxe");
		nodes.add(BASE + HISTORY + "/portfolioValue");
		nodes.add(BASE + HISTORY + "/shareQuantity");
		nodes.add(BASE + HISTORY + "/shareValue");
		nodes.add(BASE + HISTORY + "/monthlyYield");
		nodes.add(BASE + HISTORY + "/commentary");
		nodes.add(BASE + HISTORY + "/option");
		nodes.add(BASE + HISTORY + "/details");
		nodes.add(BASE + HISTORY + "/all");
		nodes.add(BASE + HISTORY + "/delete");
		for (String n : nodes) {
			node2 = xml.getNode(n);
			addToMap(map, node, node2);
		}
		node = xml.getNode(BASE + HISTORY_HIDDEN);
		nodes = new ArrayList<>();
		nodes.add(BASE + HISTORY_HIDDEN + "/refresh");
		nodes.add(BASE + HISTORY_HIDDEN + "/account");
		nodes.add(BASE + HISTORY_HIDDEN + "/movement");
		nodes.add(BASE + HISTORY_HIDDEN + "/yield");
		nodes.add(BASE + HISTORY_HIDDEN + "/buy");
		nodes.add(BASE + HISTORY_HIDDEN + "/sell");
		nodes.add(BASE + HISTORY_HIDDEN + "/taxe");
		nodes.add(BASE + HISTORY_HIDDEN + "/commentary");
		nodes.add(BASE + HISTORY_HIDDEN + "/update");
		for (String n : nodes) {
			node2 = xml.getNode(n);
			addToMap(map, node, node2);
		}
		node = xml.getNode(BASE + ACCOUNTS);
		nodes = new ArrayList<>();
		nodes.add(BASE + ACCOUNTS + "/title");
		nodes.add(BASE + ACCOUNTS + "/add");
		nodes.add(BASE + ACCOUNTS + "/account");
		nodes.add(BASE + ACCOUNTS + "/currency");
		nodes.add(BASE + ACCOUNTS + "/liquidity");
		nodes.add(BASE + ACCOUNTS + "/parity");
		nodes.add(BASE + ACCOUNTS + "/value");
		nodes.add(BASE + ACCOUNTS + "/option");
		nodes.add(BASE + ACCOUNTS + "/modify");
		nodes.add(BASE + ACCOUNTS + "/total");
		for (String n : nodes) {
			node2 = xml.getNode(n);
			addToMap(map, node, node2);
		}
		node = xml.getNode(BASE + ACCOUNTS_HIDDEN);
		nodes = new ArrayList<>();
		nodes.add(BASE + ACCOUNTS_HIDDEN + "/add");
		nodes.add(BASE + ACCOUNTS_HIDDEN + "/accountName");
		nodes.add(BASE + ACCOUNTS_HIDDEN + "/currency");
		nodes.add(BASE + ACCOUNTS_HIDDEN + "/liquidity");
		for (String n : nodes) {
			node2 = xml.getNode(n);
			addToMap(map, node, node2);
		}
		node = xml.getNode(BASE + LIST);
		nodes = new ArrayList<>();
		nodes.add(BASE + LIST + "/title");
		nodes.add(BASE + LIST + "/add");
		nodes.add(BASE + LIST + "/date");
		nodes.add(BASE + LIST + "/company");
		nodes.add(BASE + LIST + "/quote");
		nodes.add(BASE + LIST + "/lowMaxYear");
		nodes.add(BASE + LIST + "/yield");
		nodes.add(BASE + LIST + "/lowerLimit");
		nodes.add(BASE + LIST + "/higherLimit");
		nodes.add(BASE + LIST + "/info");
		nodes.add(BASE + LIST + "/modify");
		for (String n : nodes) {
			node2 = xml.getNode(n);
			addToMap(map, node, node2);
		}
		node = xml.getNode(BASE + LIST_HIDDEN);
		nodes = new ArrayList<>();
		nodes.add(BASE + LIST_HIDDEN + "/refresh");
		nodes.add(BASE + LIST_HIDDEN + "/confirm");
		nodes.add(BASE + LIST_HIDDEN + "/add");
		nodes.add(BASE + LIST_HIDDEN + "/yahooId");
		nodes.add(BASE + LIST_HIDDEN + "/lowerLimit");
		nodes.add(BASE + LIST_HIDDEN + "/higherLimit");
		nodes.add(BASE + LIST_HIDDEN + "/modify");
		for (String n : nodes) {
			node2 = xml.getNode(n);
			addToMap(map, node, node2);
		}
		node = xml.getNode(BASE + CHARTS);
		nodes = new ArrayList<>();
		nodes.add(BASE + CHARTS + "/title");
		for (String n : nodes) {
			node2 = xml.getNode(n);
			addToMap(map, node, node2);
		}
		node = xml.getNode(BASE + CURRENCIES);
		nodes = new ArrayList<>();
		nodes.add(BASE + CURRENCIES + "/title");
		nodes.add(BASE + CURRENCIES + "/portfolioCurrency");
		nodes.add(BASE + CURRENCIES + "/currency");
		nodes.add(BASE + CURRENCIES + "/lastUpdate");
		for (String n : nodes) {
			node2 = xml.getNode(n);
			addToMap(map, node, node2);
		}
		node = xml.getNode(BASE + CURRENCIES_HIDDEN);
		nodes = new ArrayList<>();
		nodes.add(BASE + CURRENCIES_HIDDEN + "/refresh");
		nodes.add(BASE + CURRENCIES_HIDDEN + "/confirm");
		for (String n : nodes) {
			node2 = xml.getNode(n);
			addToMap(map, node, node2);
		}
		node = xml.getNode(BASE + OPTIONS);
		nodes = new ArrayList<>();
		nodes.add(BASE + OPTIONS + "/title");
		nodes.add(BASE + OPTIONS + "/defautCurrency");
		nodes.add(BASE + OPTIONS + "/format");
		nodes.add(BASE + OPTIONS + "/timeZone");
		nodes.add(BASE + OPTIONS + "/datePattern");
		nodes.add(BASE + OPTIONS + "/portfolioColumn");
		nodes.add(BASE + OPTIONS + "/loadHistory");
		nodes.add(BASE + OPTIONS + "/liquidity");
		nodes.add(BASE + OPTIONS + "/account");
		nodes.add(BASE + OPTIONS + "/load");
		for (String n : nodes) {
			node2 = xml.getNode(n);
			addToMap(map, node, node2);
		}
		node = xml.getNode(BASE + OPTIONS_HIDDEN);
		nodes = new ArrayList<>();
		nodes.add(BASE + OPTIONS_HIDDEN + "/currency");
		nodes.add(BASE + OPTIONS_HIDDEN + "/format");
		nodes.add(BASE + OPTIONS_HIDDEN + "/timeZone");
		nodes.add(BASE + OPTIONS_HIDDEN + "/datePattern");
		nodes.add(BASE + OPTIONS_HIDDEN + "/columns");
		nodes.add(BASE + OPTIONS_HIDDEN + "/quote");
		nodes.add(BASE + OPTIONS_HIDDEN + "/parities");
		nodes.add(BASE + OPTIONS_HIDDEN + "/yieldTTM");
		nodes.add(BASE + OPTIONS_HIDDEN + "/yieldPerUnitCostPrice");
		nodes.add(BASE + OPTIONS_HIDDEN + "/stopLoss");
		nodes.add(BASE + OPTIONS_HIDDEN + "/objective");
		for (String n : nodes) {
			node2 = xml.getNode(n);
			addToMap(map, node, node2);
		}
		return map;
	}

	/**
	 * Add the informations to the result map
	 *
	 * @param m    the result map
	 * @param base the node base
	 * @param node the node
	 */
	protected final void addToMap(final Map<String, String> m, final Node base, final Node node) {
		m.put(base.getName().toUpperCase() + "_" + node.getName().toUpperCase(), node.getStringValue());
	}
}
