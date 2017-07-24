package fr.cph.stock.controller.account;

import fr.cph.stock.business.AccountBusiness;
import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.util.Info;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static fr.cph.stock.util.Constants.*;

@SessionAttributes(USER)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
@Controller
public class AccountController {

	@NonNull
	private AccountBusiness accountBusiness;
	@NonNull
	private UserBusiness userBusiness;

	@RequestMapping(value = "/accounts", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView history(@ModelAttribute final User user,
								@CookieValue(LANGUAGE) final String lang) {
		final ModelAndView model = new ModelAndView("accounts");
		final Portfolio portfolio = userBusiness.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));

		model.addObject(LANGUAGE, LanguageFactory.INSTANCE.getLanguage(lang));
		model.addObject(PORTFOLIO, portfolio);
		model.addObject(CURRENCIES, Currency.values());
		model.addObject(APP_TITLE, Info.NAME + " &bull;   Accounts");
		return model;
	}

	@RequestMapping(value = "/addaccount", method = RequestMethod.POST)
	public ModelAndView addAccount(@RequestParam(value = ACCOUNT) final String acc,
								   @RequestParam(value = CURRENCY) final Currency currency,
								   @RequestParam(value = LIQUIDITY) final double liquidity,
								   @ModelAttribute final User user) {
		final ModelAndView model = new ModelAndView("forward:/accounts");
		final Account account = Account.builder()
			.currency(currency)
			.liquidity(liquidity)
			.name(acc)
			.userId(user.getId())
			.del(true).build();
		accountBusiness.addAccount(account);
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
			.currency(Currency.getEnum(currency))
			.liquidity(Double.valueOf(liquidity))
			.name(acc)
			.userId(user.getId()).build();
		accountBusiness.updateAccount(account);
		model.addObject(MESSAGE, MODIFIED_MESSAGE);
		return model;
	}

	@RequestMapping(value = "/deleteaccount", method = RequestMethod.POST)
	public ModelAndView deleteAccount(@RequestParam(value = "accountId") final int id) {
		final ModelAndView model = new ModelAndView("forward:/accounts");
		final Account account = accountBusiness.getAccount(id).orElseThrow(() -> new NotFoundException("Account " + id + "not found"));
		if (account.getDel()) {
			accountBusiness.deleteAccount(account);
			model.addObject(MESSAGE, "Account deleted");
		} else {
			model.addObject(ERROR, "You are not allowed to delete this account!");
		}
		return model;
	}
}
