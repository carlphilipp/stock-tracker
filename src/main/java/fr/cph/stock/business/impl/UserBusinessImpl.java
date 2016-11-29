package fr.cph.stock.business.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import fr.cph.stock.business.CurrencyBusiness;
import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.dao.AccountDAO;
import fr.cph.stock.dao.DAO;
import fr.cph.stock.dao.PortfolioDAO;
import fr.cph.stock.dao.UserDAO;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.LoginException;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.security.SecurityService;
import fr.cph.stock.util.Info;
import fr.cph.stock.util.Mail;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Singleton
public class UserBusinessImpl implements UserBusiness {

	private static final MathContext MATHCONTEXT = MathContext.DECIMAL32;
	private static final int DB_PASSWORD_LIMIT = 64;

	@Inject
	private CurrencyBusiness currencyBusiness;
	@Inject
	private SecurityService securityService;

	private UserDAO userDAO;
	private PortfolioDAO portfolioDAO;
	private AccountDAO accountDAO;

	@Inject
	public void setUserDAO(@Named("User") final DAO dao) {
		userDAO = (UserDAO) dao;
	}

	@Inject
	public void setPortfolioDAO(@Named("Portfolio") final DAO dao) {
		portfolioDAO = (PortfolioDAO) dao;
	}

	@Inject
	public void setAccountDAO(@Named("Account") final DAO dao) {
		accountDAO = (AccountDAO) dao;
	}

	@Override
	public final void createUser(final String login, final String md5Password, final String email) throws NoSuchAlgorithmException, UnsupportedEncodingException, LoginException {
		final String md5PasswordHashed = securityService.encodeToSha256(md5Password);
		final String saltHashed = securityService.generateSalt();
		final String cryptedPasswordSalt = securityService.encodeToSha256(md5PasswordHashed + saltHashed);
		final Optional<User> userInDbWithLogin = getUser(login);
		final Optional<User> userInDbWithEmail = getUserWithEmail(email);
		if (!userInDbWithLogin.isPresent()) {
			throw new LoginException("Sorry, '" + login + "' is not available!");
		}
		if (!userInDbWithEmail.isPresent()) {
			throw new LoginException("Sorry, '" + email + "' is not available!");
		}
		final User user = User.builder()
			.login(login)
			.password(saltHashed + cryptedPasswordSalt)
			.email(email)
			.allow(false)
			.build();
		userDAO.insert(user);
		final StringBuilder body = new StringBuilder();
		final String check = securityService.encodeToSha256(login + saltHashed + cryptedPasswordSalt + email);
		body.append("Welcome to ")
			.append(Info.NAME)
			.append(",\n\nPlease valid your account by clicking on that link:")
			.append(Info.ADDRESS)
			.append(Info.FOLDER)
			.append("/check?&login=")
			.append(login)
			.append("&check=")
			.append(check)
			.append(".\n\nBest regards,\nThe ")
			.append(Info.NAME)
			.append(" team.");
		Mail.sendMail("[Registration] " + Info.NAME, body.toString(), new String[]{email}, null);
		createUserPortfolio(user.getLogin());
		createUserDefaultAccount(user);
	}

	@Override
	public final Optional<User> getUser(final String login) {
		return userDAO.selectWithLogin(login);
	}

	@Override
	public final Optional<User> getUserWithEmail(final String email) {
		return userDAO.selectWithEmail(email);
	}

	@Override
	public final void deleteUser(final String login) {
		userDAO.delete(User.builder().login(login).build());
	}

	@Override
	public final void validateUser(final String login) {
		final User user = userDAO.selectWithLogin(login).orElseThrow(() -> new NotFoundException(login));
		user.setAllow(true);
		userDAO.update(user);
	}

	@Override
	public final void updateUser(final User user) {
		userDAO.update(user);
	}

	/**
	 * @param login the login
	 */
	private void createUserPortfolio(final String login) {
		final User user = userDAO.selectWithLogin(login).orElseThrow(() -> new NotFoundException(login));
		final Portfolio portfolio = new Portfolio();
		portfolio.setCurrency(Currency.EUR);
		portfolio.setUserId(user.getId());
		portfolioDAO.insert(portfolio);
	}

	/**
	 * @param u the user
	 */
	private void createUserDefaultAccount(final User u) {
		final User user = userDAO.selectWithLogin(u.getLogin()).orElseThrow(() -> new NotFoundException(u.getLogin()));
		final Account account = Account.builder()
			.currency(Currency.EUR)
			.liquidity(0.0)
			.name("Default")
			.userId(user.getId())
			.del(false).build();
		accountDAO.insert(account);
	}

	@Override
	public final Optional<User> checkUser(final String login, final String md5Password) throws LoginException {
		Optional<User> userOptional = userDAO.selectWithLogin(login);
		if (userOptional.isPresent()) {
			final User user = userOptional.get();
			try {
				final String md5PasswordHashed = securityService.encodeToSha256(md5Password);
				final String saltHashed = user.getPassword().substring(0, DB_PASSWORD_LIMIT);
				final String cryptedPasswordSalt = user.getPassword().substring(DB_PASSWORD_LIMIT, user.getPassword().length());
				final String cryptedPasswordSaltToTest = securityService.encodeToSha256(md5PasswordHashed + saltHashed);
				if (!cryptedPasswordSalt.equals(cryptedPasswordSaltToTest)) {
					userOptional = Optional.empty();
				} else {
					user.setPassword(null);
				}
			} catch (final NoSuchAlgorithmException | UnsupportedEncodingException e) {
				throw new LoginException(e.getMessage(), e);
			}
		}
		return userOptional;
	}

	@Override
	public final void updateOneUserPassword(final User user) {
		userDAO.updateOneUserPassword(user);
	}

	@Override
	public final Optional<Portfolio> getUserPortfolio(final int userId, final Date from, final Date to) throws YahooException {
		final Optional<Portfolio> portfolioOptional = portfolioDAO.selectPortfolioFromUserIdWithEquities(userId, from, to);
		if (portfolioOptional.isPresent()) {
			final Portfolio portfolio = portfolioOptional.get();
			Collections.sort(portfolio.getEquities());
			final Currency currency = currencyBusiness.loadCurrencyData(portfolio.getCurrency());
			portfolio.setCurrency(currency);
			for (final Equity e : portfolio.getEquities()) {
				final double parity = e.getCompany().getCurrency() == portfolio.getCurrency()
					? 1.0 : portfolio.getCurrency().getParity(e.getCompany().getCurrency());
				e.setParity(parity);
			}
			double liquidity = 0.0;
			for (final Account acc : portfolio.getAccounts()) {
				if (acc.getCurrency() == portfolio.getCurrency()) {
					liquidity += acc.getLiquidity();
					acc.setParity(1.0);
				} else {
					liquidity += acc.getLiquidity() * portfolio.getCurrency().getParity(acc.getCurrency());
					acc.setParity(portfolio.getCurrency().getParity(acc.getCurrency()));
				}
			}
			liquidity = new BigDecimal(Double.toString(liquidity), MATHCONTEXT).doubleValue();
			portfolio.setLiquidity(liquidity);
			portfolio.compute();
		}
		return portfolioOptional;
	}

	@Override
	public final Optional<Portfolio> getUserPortfolio(final int userId) throws YahooException {
		return getUserPortfolio(userId, null, null);
	}

	@Override
	public final void updatePortfolio(final Portfolio portfolio) {
		portfolioDAO.update(portfolio);
	}

	@Override
	public final void updateLiquidity(final Account account, final double liquidity) {
		account.setLiquidity(liquidity);
		accountDAO.update(account);
	}
}
