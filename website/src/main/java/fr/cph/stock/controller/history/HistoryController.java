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

package fr.cph.stock.controller.history;

import fr.cph.stock.config.AppProperties;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.ShareValue;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.service.ShareValueService;
import fr.cph.stock.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called when the user want to access to the history page
 *
 * @author Carl-Philipp Harmant
 */
@SessionAttributes(USER)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
@Controller
public class HistoryController {

	private static final int ITEM_MAX = 20;
	private static final MathContext MATH_CONTEXT = MathContext.DECIMAL32;

	@NonNull
	private AppProperties appProperties;
	@NonNull
	private UserService userService;
	@NonNull
	private ShareValueService shareValueService;

	@RequestMapping(value = "/history", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView history(@RequestParam(value = PAGE, defaultValue = "1") final int pageNumber,
								@Valid @ModelAttribute final User user,
								@CookieValue(LANGUAGE) final String lang) throws ServletException {
		final ModelAndView model = new ModelAndView("sharevalue");
		try {
			final Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
			if (portfolio.getShareValues().size() != 0) {
				int begin = pageNumber * ITEM_MAX - ITEM_MAX;
				int end = pageNumber * ITEM_MAX - 1;
				int nbPage = portfolio.getShareValues().size() / ITEM_MAX + 1;
				if (pageNumber == 0) {
					begin = 0;
					end = portfolio.getShareValues().size() - 1;
				}
				if (pageNumber == nbPage) {
					end = portfolio.getShareValues().size() - 1;
				}
				model.addObject(BEGIN, begin);
				model.addObject(END, end);
				model.addObject(PAGE, pageNumber);
				model.addObject(NB_PAGE, nbPage);
			}
			model.addObject(PORTFOLIO, portfolio);
		} catch (final YahooException e) {
			log.error(e.getMessage(), e);
			throw new ServletException("Error: " + e.getMessage(), e);
		}
		model.addObject(LANGUAGE, LanguageFactory.INSTANCE.getLanguage(lang));
		model.addObject(APP_TITLE, appProperties.getName() + " &bull; History");
		return model;
	}

	@RequestMapping(value = "/updatesharevalue", method = RequestMethod.POST)
	public ModelAndView updateShareValue(
		@RequestParam(value = ACCOUNT) final int acc,
		@RequestParam(value = MOVEMENT) final double movement,
		@RequestParam(value = YIELD) final double yield,
		@RequestParam(value = BUY) final double buy,
		@RequestParam(value = SELL) final double sell,
		@RequestParam(value = TAXE) final double tax,
		@RequestParam(value = COMMENTARY, required = false) final String commentary,
		@Valid @ModelAttribute final User user) throws ServletException {
		final ModelAndView model = new ModelAndView("forward:/history");
		String message = null;
		try {
			Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
			final Account account = portfolio.getAccount(acc).orElseThrow(() -> new NotFoundException(acc));
			double newLiquidity = account.getLiquidity() + movement + yield - buy + sell - tax;
			newLiquidity = new BigDecimal(Double.toString(newLiquidity), MATH_CONTEXT).doubleValue();
			userService.updateLiquidity(account, newLiquidity);
			message = ("'" + account.getName() + "' liquidity new value: " + newLiquidity);
			portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
			shareValueService.updateCurrentShareValue(portfolio, account, movement, yield, buy, sell, tax, commentary);
		} catch (final YahooException e) {
			log.error(e.getMessage(), e);
		}
		model.addObject(MESSAGE, message);
		return model;
	}

	@RequestMapping(value = "/updatecommentonsharevalue", method = RequestMethod.POST)
	public ModelAndView updateCommentOnShareValue(
		@RequestParam(value = COMMENTARY_UPDATED) final String commentary,
		@RequestParam(value = SHARE_ID) final int shareId) throws ServletException {
		final ModelAndView model = new ModelAndView("forward:/history");
		final ShareValue sv = shareValueService.selectOneShareValue(shareId).orElseThrow(() -> new NotFoundException(shareId));
		sv.setCommentary(commentary);
		shareValueService.updateCommentaryShareValue(sv);
		model.addObject(MESSAGE, "Modified!");
		return model;
	}

	@RequestMapping(value = "/deletesharevalue", method = RequestMethod.POST)
	public ModelAndView updateCommentOnShareValue(
		@RequestParam(value = SHARE_ID) final int shareId,
		@RequestParam(value = LIQUIDITY_MOVEMENT) final double liquidityMovement,
		@RequestParam(value = YIELD) final double yield,
		@RequestParam(value = BUY) final double buy,
		@RequestParam(value = SELL) final double sell,
		@RequestParam(value = TAXE) final double tax,
		@RequestParam(value = ACCOUNT) final String accountName,
		@Valid @ModelAttribute final User user) {
		final ModelAndView model = new ModelAndView("forward:/history");
		final StringBuilder message = new StringBuilder();

		final Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
		final Optional<Account> account = portfolio.getAccount(accountName);
		final ShareValue shareValue = new ShareValue();
		shareValue.setId(shareId);
		if (!account.isPresent()) {
			shareValueService.deleteShareValue(shareValue);
			message.append("Account not found, probably deleted before. Line has still been deleted!");
			model.addObject(WARN, message);
		} else {
			// Update account total
			double total = liquidityMovement + yield - buy + sell - tax;
			total = new BigDecimal(Double.toString(total), MATH_CONTEXT).doubleValue();
			if (total != 0.0) {
				double newLiquidity = account.get().getLiquidity() - total;
				userService.updateLiquidity(account.get(), new BigDecimal(Double.toString(newLiquidity), MATH_CONTEXT).doubleValue());
				message.append("Liquidity new value: ").append((new BigDecimal(Double.toString(newLiquidity), MATH_CONTEXT)).doubleValue()).append("<br>");
			}
		}
		shareValueService.deleteShareValue(shareValue);
		message.append("Done !");
		model.addObject(MESSAGE, message);
		return model;
	}
}
