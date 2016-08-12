package fr.cph.stock.business.impl;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.dao.AccountDAO;
import fr.cph.stock.dao.PortfolioDAO;
import fr.cph.stock.dao.UserDAO;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.LoginException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.security.Security;
import fr.cph.stock.util.Info;
import fr.cph.stock.util.Mail;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Date;

public enum UserBusinessImpl implements UserBusiness {

	INSTANCE;

	private static final MathContext MATHCONTEXT = MathContext.DECIMAL32;

	private final Business business;
	private final UserDAO userDAO;
	private final PortfolioDAO portfolioDAO;
	private final AccountDAO accountDAO;


	UserBusinessImpl() {
		business = BusinessImpl.INSTANCE;
		userDAO = new UserDAO();
		portfolioDAO = new PortfolioDAO();
		accountDAO = new AccountDAO();
	}

	@Override
	public final void createUser(final String login, final String md5Password, final String email) throws NoSuchAlgorithmException,
		UnsupportedEncodingException, LoginException {
		String md5PasswordHashed = Security.encodeToSha256(md5Password);
		String saltHashed = Security.generateSalt();
		String cryptedPasswordSalt = Security.encodeToSha256(md5PasswordHashed + saltHashed);
		User userInDbWithLogin = getUser(login);
		User userInDbWithEmail = getUserWithEmail(email);
		if (userInDbWithLogin != null) {
			throw new LoginException("Sorry, '" + login + "' is not available!");
		}
		if (userInDbWithEmail != null) {
			throw new LoginException("Sorry, '" + email + "' is not available!");
		}
		User user = new User();
		user.setLogin(login);
		user.setPassword(saltHashed + cryptedPasswordSalt);
		user.setEmail(email);
		user.setAllow(false);
		userDAO.insert(user);
		StringBuilder body = new StringBuilder();
		String check = Security.encodeToSha256(login + saltHashed + cryptedPasswordSalt + email);
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
	public final User getUser(final String login) {
		return userDAO.selectWithLogin(login);
	}

	@Override
	public final User getUserWithEmail(final String email) {
		return userDAO.selectWithEmail(email);
	}

	@Override
	public final void deleteUser(final String login) {
		User user = new User();
		user.setLogin(login);
		userDAO.delete(user);
	}

	@Override
	public final void validateUser(final String login) {
		final User user = userDAO.selectWithLogin(login);
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
		final User user = userDAO.selectWithLogin(login);
		final Portfolio portfolio = new Portfolio();
		portfolio.setCurrency(Currency.EUR);
		portfolio.setUserId(user.getId());
		portfolioDAO.insert(portfolio);
	}

	/**
	 * @param u the user
	 */
	private void createUserDefaultAccount(final User u) {
		final User user = userDAO.selectWithLogin(u.getLogin());
		final Account account = new Account();
		account.setCurrency(Currency.EUR);
		account.setLiquidity(0.0);
		account.setName("Default");
		account.setUserId(user.getId());
		account.setDel(false);
		accountDAO.insert(account);
	}

	@Override
	public final User checkUser(final String login, final String md5Password) throws LoginException {
		User user = userDAO.selectWithLogin(login);
		final int sixtyFour = 64;
		if (user != null) {
			String md5PasswordHashed;
			try {
				md5PasswordHashed = Security.encodeToSha256(md5Password);
				String saltHashed = user.getPassword().substring(0, sixtyFour);
				String cryptedPasswordSalt = user.getPassword().substring(sixtyFour, user.getPassword().length());
				String cryptedPasswordSaltToTest = Security.encodeToSha256(md5PasswordHashed + saltHashed);
				if (!cryptedPasswordSalt.equals(cryptedPasswordSaltToTest)) {
					user = null;
				} else {
					user.setPassword(null);
				}
			} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
				throw new LoginException(e.getMessage(), e);
			}
		}
		return user;
	}

	@Override
	public final void updateOneUserPassword(final User user) {
		userDAO.updateOneUserPassword(user);
	}

	@Override
	public final Portfolio getUserPortfolio(final int userId, final Date from, final Date to) throws YahooException {
		Portfolio portfolio = portfolioDAO.selectPortfolioFromUserIdWithEquities(userId, from, to);
		Collections.sort(portfolio.getEquities());
		Currency currency = business.loadCurrencyData(portfolio.getCurrency());
		portfolio.setCurrency(currency);
		for (Equity e : portfolio.getEquities()) {
			if (e.getCompany().getCurrency() == portfolio.getCurrency()) {
				e.setParity(1.0);
			} else {
				e.setParity(portfolio.getCurrency().getParity(e.getCompany().getCurrency()));
			}
		}
		double liquidity = 0.0;
		for (Account acc : portfolio.getAccounts()) {
			if (acc.getCurrency() == portfolio.getCurrency()) {
				liquidity += acc.getLiquidity();
				acc.setParity(1.0);
			} else {
				liquidity += acc.getLiquidity() * portfolio.getCurrency().getParity(acc.getCurrency());
				acc.setParity(portfolio.getCurrency().getParity(acc.getCurrency()));
			}
		}
		liquidity = new BigDecimal(liquidity, MATHCONTEXT).doubleValue();
		portfolio.setLiquidity(liquidity);
		portfolio.compute();
		return portfolio;
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
