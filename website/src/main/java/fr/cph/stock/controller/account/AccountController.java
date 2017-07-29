package fr.cph.stock.controller.account;

import fr.cph.stock.config.AppProperties;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
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

import javax.annotation.PostConstruct;
import java.util.*;

import static fr.cph.stock.util.Constants.*;

@SessionAttributes(USER)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
@Controller
public class AccountController {

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
								@CookieValue(LANGUAGE) final String lang) {
		final ModelAndView model = new ModelAndView("accounts");
		final Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));

		model.addObject(LANGUAGE, LanguageFactory.INSTANCE.getLanguage(lang));
		model.addObject(PORTFOLIO, portfolio);
		model.addObject(CURRENCIES, fr.cph.stock.enumtype.Currency.values());
		model.addObject(APP_TITLE, appProperties.getName() + " &bull;   Accounts");
		return model;
	}

	@RequestMapping(value = "/addaccount", method = RequestMethod.POST)
	public ModelAndView addAccount(@RequestParam(value = ACCOUNT) final String acc,
								   @RequestParam(value = CURRENCY) final fr.cph.stock.enumtype.Currency currency,
								   @RequestParam(value = LIQUIDITY) final double liquidity,
								   @ModelAttribute final User user) {
		final ModelAndView model = new ModelAndView("forward:/accounts");
		final Account account = Account.builder()
			.currency(currency)
			.liquidity(liquidity)
			.name(acc)
			.userId(user.getId())
			.del(true).build();
		accountService.addAccount(account);
		model.addObject(MESSAGE, ADDED);
		return model;
	}

	@RequestMapping(value = "/editaccount", method = RequestMethod.POST)
	public ModelAndView editAccount(@RequestParam(value = "accountId") final int id,
									@RequestParam(value = ACCOUNT) final String acc,
									@RequestParam(value = CURRENCY) final String currency,
									@RequestParam(value = LIQUIDITY) final String liquidity,
									@ModelAttribute final User user) {
		final ModelAndView model = new ModelAndView("forward:/accounts");
		final Account account = Account.builder()
			.id(id)
			.currency(fr.cph.stock.enumtype.Currency.getEnum(currency))
			.liquidity(Double.valueOf(liquidity))
			.name(acc)
			.userId(user.getId()).build();
		accountService.updateAccount(account);
		model.addObject(MESSAGE, MODIFIED_MESSAGE);
		return model;
	}

	@RequestMapping(value = "/deleteaccount", method = RequestMethod.POST)
	public ModelAndView deleteAccount(@RequestParam(value = "accountId") final int id) {
		final ModelAndView model = new ModelAndView("forward:/accounts");
		final Account account = accountService.getAccount(id).orElseThrow(() -> new NotFoundException("Account " + id + "not found"));
		if (account.getDel()) {
			accountService.deleteAccount(account);
			model.addObject(MESSAGE, "Account deleted");
		} else {
			model.addObject(ERROR, "You are not allowed to delete this account!");
		}
		return model;
	}
}
