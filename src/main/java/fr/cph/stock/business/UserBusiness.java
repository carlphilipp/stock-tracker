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

	void createUser(String login, String md5Password, String email) throws NoSuchAlgorithmException, UnsupportedEncodingException, LoginException;

	User getUser(String login);

	User getUserWithEmail(String email);

	void deleteUser(String login);

	void validateUser(String login);

	void updateUser(User user);

	User checkUser(String login, String md5Password) throws LoginException;

	void updateOneUserPassword(User user);

	Portfolio getUserPortfolio(int userId, Date from, Date to) throws YahooException;

	Portfolio getUserPortfolio(int userId) throws YahooException;

	void updatePortfolio(Portfolio portfolio);

	void updateLiquidity(Account account, double liquidity);
}
