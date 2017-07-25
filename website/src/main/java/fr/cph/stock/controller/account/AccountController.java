package fr.cph.stock.controller.account;

import fr.cph.stock.config.AppProperties;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.service.AccountService;
import fr.cph.stock.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import fr.cph.stock.util.Constants;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@SessionAttributes(Constants.USER)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
@Controller
public class AccountController {

	private static final int ONE_YEAR_COOKIE = 60 * 60 * 24 * 365;
	private static final List<String> FORMAT_LIST = Arrays.asList(Locale.getISOLanguages());
	private static final List<String> TIME_ZONE_LIST = Arrays.asList(TimeZone.getAvailableIDs());

	@NonNull
	private AppProperties appProperties;
	@NonNull
	private AccountService accountService;
	@NonNull
	private UserService userService;

	@PostConstruct
	public void init() {
		Collections.sort(FORMAT_LIST);
		Collections.sort(TIME_ZONE_LIST);
	}

	@RequestMapping(value = "/accounts", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView history(@ModelAttribute final User user,
								@CookieValue(Constants.LANGUAGE) final String lang) {
		final ModelAndView model = new ModelAndView("accounts");
		final Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));

		model.addObject(Constants.LANGUAGE, LanguageFactory.INSTANCE.getLanguage(lang));
		model.addObject(Constants.PORTFOLIO, portfolio);
		model.addObject(Constants.CURRENCIES, fr.cph.stock.enumtype.Currency.values());
		model.addObject(Constants.APP_TITLE, appProperties.getName() + " &bull;   Accounts");
		return model;
	}

	@RequestMapping(value = "/addaccount", method = RequestMethod.POST)
	public ModelAndView addAccount(@RequestParam(value = Constants.ACCOUNT) final String acc,
								   @RequestParam(value = Constants.CURRENCY) final fr.cph.stock.enumtype.Currency currency,
								   @RequestParam(value = Constants.LIQUIDITY) final double liquidity,
								   @ModelAttribute final User user) {
		final ModelAndView model = new ModelAndView("forward:/accounts");
		final Account account = Account.builder()
			.currency(currency)
			.liquidity(liquidity)
			.name(acc)
			.userId(user.getId())
			.del(true).build();
		accountService.addAccount(account);
		model.addObject(Constants.MESSAGE, Constants.ADDED);
		return model;
	}

	@RequestMapping(value = "/editaccount", method = RequestMethod.POST)
	public ModelAndView editAccount(@RequestParam(value = "accountId") final int id,
									@RequestParam(value = Constants.ACCOUNT) final String acc,
									@RequestParam(value = Constants.CURRENCY) final String currency,
									@RequestParam(value = Constants.LIQUIDITY) final String liquidity,
									@ModelAttribute final User user) {
		final ModelAndView model = new ModelAndView("forward:/accounts");
		final Account account = Account.builder()
			.id(id)
			.currency(fr.cph.stock.enumtype.Currency.getEnum(currency))
			.liquidity(Double.valueOf(liquidity))
			.name(acc)
			.userId(user.getId()).build();
		accountService.updateAccount(account);
		model.addObject(Constants.MESSAGE, Constants.MODIFIED_MESSAGE);
		return model;
	}

	@RequestMapping(value = "/deleteaccount", method = RequestMethod.POST)
	public ModelAndView deleteAccount(@RequestParam(value = "accountId") final int id) {
		final ModelAndView model = new ModelAndView("forward:/accounts");
		final Account account = accountService.getAccount(id).orElseThrow(() -> new NotFoundException("Account " + id + "not found"));
		if (account.getDel()) {
			accountService.deleteAccount(account);
			model.addObject(Constants.MESSAGE, "Account deleted");
		} else {
			model.addObject(Constants.ERROR, "You are not allowed to delete this account!");
		}
		return model;
	}

	// TODO REFACTOR
	@RequestMapping(value = "/options", method = RequestMethod.GET)
	public ModelAndView options(final HttpServletRequest request,
								@ModelAttribute final User user,
								@CookieValue(Constants.LANGUAGE) final String lang) {
		final ModelAndView model = new ModelAndView("options");
		Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
		String quoteRes = null, currencyRes = null, parityRes = null, stopLossRes = null, objectiveRes = null, yield1Res = null, yield2Res = null;
		final Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			switch (cookie.getName()) {
				case Constants.QUOTE:
					quoteRes = cookie.getValue();
					break;
				case Constants.CURRENCY:
					currencyRes = cookie.getValue();
					break;
				case Constants.PARITY:
					parityRes = cookie.getValue();
					break;
				case Constants.STOP_LOSS:
					stopLossRes = cookie.getValue();
					break;
				case Constants.OBJECTIVE:
					objectiveRes = cookie.getValue();
					break;
				case Constants.YIELD_1:
					yield1Res = cookie.getValue();
					break;
				case Constants.YIELD_2:
					yield2Res = cookie.getValue();
					break;
				default:
					break;
			}
		}
		//}
		model.addObject(Constants.QUOTE, quoteRes);
		model.addObject(Constants.CURRENCY, currencyRes);
		model.addObject(Constants.PARITY, parityRes);
		model.addObject(Constants.STOP_LOSS, stopLossRes);
		model.addObject(Constants.OBJECTIVE, objectiveRes);
		model.addObject(Constants.YIELD_1, yield1Res);
		model.addObject(Constants.YIELD_2, yield2Res);

		model.addObject(Constants.PORTFOLIO, portfolio);
		model.addObject(Constants.CURRENCIES, fr.cph.stock.enumtype.Currency.values());
		model.addObject(Constants.FORMAT, FORMAT_LIST);
		model.addObject(Constants.TIME_ZONE, TIME_ZONE_LIST);

		model.addObject(Constants.LANGUAGE, LanguageFactory.INSTANCE.getLanguage(lang));
		model.addObject(Constants.APP_TITLE, appProperties.getName() + " &bull; Options");
		return model;
	}

	// TODO REFACTOR
	@RequestMapping(value = "/options", method = RequestMethod.POST)
	public ModelAndView updateOptions(final HttpServletRequest request,
									  final HttpServletResponse response,
									  @RequestParam(value = Constants.CURRENCY) final fr.cph.stock.enumtype.Currency currency,
									  @RequestParam(value = Constants.FORMAT) final String format,
									  @RequestParam(value = Constants.TIME_ZONE) final String timeZone,
									  @RequestParam(value = Constants.DATE_PATTERN) final String datePattern,
									  @RequestParam(value = Constants.AUTO_UPDATE) final String autoUpdate,
									  @RequestParam(value = Constants.QUOTE) final String quote,
									  @RequestParam(value = Constants.CURRENCY_2) final String currency2,
									  @RequestParam(value = Constants.PARITY) final String parity,
									  @RequestParam(value = Constants.STOP_LOSS) final String stopLoss,
									  @RequestParam(value = Constants.OBJECTIVE) final String objective,
									  @RequestParam(value = Constants.YIELD_1) final String yield1,
									  @RequestParam(value = Constants.YIELD_2) final String yield2,
									  @RequestParam(value = Constants.UPDATE_TIME, required = false) Integer updateTime,
									  @ModelAttribute final User user,
									  @CookieValue(Constants.LANGUAGE) final String lang) {
		final ModelAndView model = new ModelAndView("options");
		final Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
		String quoteRes = null, currencyRes = null, parityRes = null, stopLossRes = null, objectiveRes = null, yield1Res = null, yield2Res = null;
		String updateSendMail = null;
		if (autoUpdate == null) {
			updateTime = null;
		} else {
			updateTime = Integer.valueOf(request.getParameter(Constants.UPDATE_TIME));
			updateSendMail = request.getParameter(Constants.AUTO_UPDATE_EMAIL);
		}

		if (currency != portfolio.getCurrency()) {
			portfolio.setCurrency(currency);
			userService.updatePortfolio(portfolio);
		}
		user.setLocale(format);
		user.setTimeZone(timeZone);
		user.setDatePattern(datePattern);
		user.setUpdateHourTime(updateTime);
		if (updateSendMail != null) {
			user.setUpdateSendMail(true);
		} else {
			user.setUpdateSendMail(false);
		}
		userService.updateUser(user);

		boolean bool = addCookieToResponse(response, Constants.QUOTE, quote);
		if (bool) {
			quoteRes = Constants.CHECKED;
		}
		bool = addCookieToResponse(response, Constants.CURRENCY, currency2);
		if (bool) {
			currencyRes = Constants.CHECKED;
		}
		bool = addCookieToResponse(response, Constants.PARITY, parity);
		if (bool) {
			parityRes = Constants.CHECKED;
		}
		bool = addCookieToResponse(response, Constants.STOP_LOSS, stopLoss);
		if (bool) {
			stopLossRes = Constants.CHECKED;
		}
		bool = addCookieToResponse(response, Constants.OBJECTIVE, objective);
		if (bool) {
			objectiveRes = Constants.CHECKED;
		}
		bool = addCookieToResponse(response, Constants.YIELD_1, yield1);
		if (bool) {
			yield1Res = Constants.CHECKED;
		}
		bool = addCookieToResponse(response, Constants.YIELD_2, yield2);
		if (bool) {
			yield2Res = Constants.CHECKED;
		}
		model.addObject(Constants.QUOTE, quoteRes);
		model.addObject(Constants.CURRENCY, currencyRes);
		model.addObject(Constants.PARITY, parityRes);
		model.addObject(Constants.STOP_LOSS, stopLossRes);
		model.addObject(Constants.OBJECTIVE, objectiveRes);
		model.addObject(Constants.YIELD_1, yield1Res);
		model.addObject(Constants.YIELD_2, yield2Res);
		model.addObject(Constants.PORTFOLIO, portfolio);
		model.addObject(Constants.CURRENCIES, Currency.values());
		model.addObject(Constants.FORMAT, FORMAT_LIST);
		model.addObject(Constants.TIME_ZONE, TIME_ZONE_LIST);
		model.addObject(Constants.LANGUAGE, LanguageFactory.INSTANCE.getLanguage(lang));
		model.addObject(Constants.APP_TITLE, appProperties.getName() + " &bull; Options");
		model.addObject(Constants.UPDATED, "Done!");
		return model;
	}

	private boolean addCookieToResponse(final HttpServletResponse response, final String cookieName, final String checked) {
		boolean res = false;
		final String value;
		if (checked != null) {
			value = Constants.CHECKED;
			res = true;
		} else {
			value = "";
		}
		final Cookie cookie = new Cookie(cookieName, value);
		cookie.setMaxAge(ONE_YEAR_COOKIE);
		response.addCookie(cookie);
		return res;
	}
}
