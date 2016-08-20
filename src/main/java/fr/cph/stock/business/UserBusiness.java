package fr.cph.stock.business;

import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.LoginException;
import fr.cph.stock.exception.YahooException;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public interface UserBusiness {

	void createUser(final String login, final String md5Password, final String email) throws NoSuchAlgorithmException, UnsupportedEncodingException, LoginException;

	User getUser(final String login);

	User getUserWithEmail(final String email);

	void deleteUser(final String login);

	void validateUser(final String login);

	void updateUser(final User user);

	User checkUser(final String login, final String md5Password) throws LoginException;

	void updateOneUserPassword(final User user);

	Portfolio getUserPortfolio(final int userId, final Date from, final Date to) throws YahooException;

	void updatePortfolio(final Portfolio portfolio);

	void updateLiquidity(final Account account, final double liquidity);
}
