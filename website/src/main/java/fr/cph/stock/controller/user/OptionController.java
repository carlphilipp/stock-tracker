package fr.cph.stock.controller.user;

import fr.cph.stock.config.AppProperties;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static fr.cph.stock.util.Constants.*;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
@Controller
public class OptionController {

	private static final int ONE_YEAR_COOKIE = 60 * 60 * 24 * 365;
	private static final List<String> FORMAT_LIST = Arrays.asList(Locale.getISOLanguages());
	private static final List<String> TIME_ZONE_LIST = Arrays.asList(TimeZone.getAvailableIDs());

	@NonNull
	private AppProperties appProperties;
	@NonNull
	private UserService userService;

	// TODO REFACTOR
	@RequestMapping(value = "/options", method = RequestMethod.GET)
	public ModelAndView options(final HttpServletRequest request,
								@ModelAttribute final User user,
								@CookieValue(LANGUAGE) final String lang) {
		final ModelAndView model = new ModelAndView("options");
		Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
		String quoteRes = null, currencyRes = null, parityRes = null, stopLossRes = null, objectiveRes = null, yield1Res = null, yield2Res = null;
		final Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			switch (cookie.getName()) {
				case QUOTE:
					quoteRes = cookie.getValue();
					break;
				case CURRENCY:
					currencyRes = cookie.getValue();
					break;
				case PARITY:
					parityRes = cookie.getValue();
					break;
				case STOP_LOSS:
					stopLossRes = cookie.getValue();
					break;
				case OBJECTIVE:
					objectiveRes = cookie.getValue();
					break;
				case YIELD_1:
					yield1Res = cookie.getValue();
					break;
				case YIELD_2:
					yield2Res = cookie.getValue();
					break;
				default:
					break;
			}
		}
		model.addObject(QUOTE, quoteRes);
		model.addObject(CURRENCY, currencyRes);
		model.addObject(PARITY, parityRes);
		model.addObject(STOP_LOSS, stopLossRes);
		model.addObject(OBJECTIVE, objectiveRes);
		model.addObject(YIELD_1, yield1Res);
		model.addObject(YIELD_2, yield2Res);

		model.addObject(PORTFOLIO, portfolio);
		model.addObject(CURRENCIES, Currency.values());
		model.addObject(FORMAT, FORMAT_LIST);
		model.addObject(TIME_ZONE, TIME_ZONE_LIST);

		model.addObject(LANGUAGE, LanguageFactory.INSTANCE.getLanguage(lang));
		model.addObject(APP_TITLE, appProperties.getName() + " &bull; Options");
		return model;
	}

	// TODO REFACTOR
	@RequestMapping(value = "/options", method = RequestMethod.POST)
	public ModelAndView updateOptions(final HttpServletRequest request,
									  final HttpServletResponse response,
									  @RequestParam(value = CURRENCY) final Currency currency,
									  @RequestParam(value = FORMAT) final String format,
									  @RequestParam(value = TIME_ZONE) final String timeZone,
									  @RequestParam(value = DATE_PATTERN) final String datePattern,
									  @RequestParam(value = AUTO_UPDATE, required = false) final String autoUpdate,
									  @RequestParam(value = QUOTE, required = false) final String quote,
									  @RequestParam(value = CURRENCY_2, required = false) final String currency2,
									  @RequestParam(value = PARITY, required = false) final String parity,
									  @RequestParam(value = STOP_LOSS, required = false) final String stopLoss,
									  @RequestParam(value = OBJECTIVE, required = false) final String objective,
									  @RequestParam(value = YIELD_1, required = false) final String yield1,
									  @RequestParam(value = YIELD_2, required = false) final String yield2,
									  @RequestParam(value = UPDATE_TIME, required = false) final Integer updateTime,
									  @RequestParam(value = AUTO_UPDATE_EMAIL, required = false, defaultValue = "false") final boolean updateSendMail,
									  @ModelAttribute final User user,
									  @CookieValue(LANGUAGE) final String lang) {
		final ModelAndView model = new ModelAndView("options");
		final Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
		String quoteRes = null, currencyRes = null, parityRes = null, stopLossRes = null, objectiveRes = null, yield1Res = null, yield2Res = null;
		if (currency != portfolio.getCurrency()) {
			portfolio.setCurrency(currency);
			userService.updatePortfolio(portfolio);
		}
		user.setLocale(format);
		user.setTimeZone(timeZone);
		user.setDatePattern(datePattern);
		user.setUpdateHourTime(updateTime);
		user.setUpdateSendMail(updateSendMail);
		userService.updateUser(user);

		boolean bool = addCookieToResponse(response, QUOTE, quote);
		if (bool) {
			quoteRes = CHECKED;
		}
		bool = addCookieToResponse(response, CURRENCY, currency2);
		if (bool) {
			currencyRes = CHECKED;
		}
		bool = addCookieToResponse(response, PARITY, parity);
		if (bool) {
			parityRes = CHECKED;
		}
		bool = addCookieToResponse(response, STOP_LOSS, stopLoss);
		if (bool) {
			stopLossRes = CHECKED;
		}
		bool = addCookieToResponse(response, OBJECTIVE, objective);
		if (bool) {
			objectiveRes = CHECKED;
		}
		bool = addCookieToResponse(response, YIELD_1, yield1);
		if (bool) {
			yield1Res = CHECKED;
		}
		bool = addCookieToResponse(response, YIELD_2, yield2);
		if (bool) {
			yield2Res = CHECKED;
		}
		model.addObject(QUOTE, quoteRes);
		model.addObject(CURRENCY, currencyRes);
		model.addObject(PARITY, parityRes);
		model.addObject(STOP_LOSS, stopLossRes);
		model.addObject(OBJECTIVE, objectiveRes);
		model.addObject(YIELD_1, yield1Res);
		model.addObject(YIELD_2, yield2Res);
		model.addObject(PORTFOLIO, portfolio);
		model.addObject(CURRENCIES, Currency.values());
		model.addObject(FORMAT, FORMAT_LIST);
		model.addObject(TIME_ZONE, TIME_ZONE_LIST);
		model.addObject(LANGUAGE, LanguageFactory.INSTANCE.getLanguage(lang));
		model.addObject(APP_TITLE, appProperties.getName() + " &bull; Options");
		model.addObject(UPDATED, "Done!");
		return model;
	}

	@RequestMapping(value = "/language", method = RequestMethod.GET)
	public ModelAndView updateLanguage(final HttpServletResponse response, @RequestParam(value = LANGUAGE) final String language) {
		final ModelAndView model = new ModelAndView("redirect:/" + HOME);
		if (language.equals(ENGLISH) || language.equals(FRANCAIS)) {
			final Cookie cookie = new Cookie(LANGUAGE, language.intern());
			response.addCookie(cookie);
		}
		return model;
	}

	private boolean addCookieToResponse(final HttpServletResponse response, final String cookieName, final String checked) {
		boolean res = false;
		final String value;
		if (checked != null) {
			value = CHECKED;
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
