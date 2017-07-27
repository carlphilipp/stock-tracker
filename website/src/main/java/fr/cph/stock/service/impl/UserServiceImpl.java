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

package fr.cph.stock.service.impl;

import fr.cph.stock.config.AppProperties;
import fr.cph.stock.entities.*;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.LoginException;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.repository.AccountRepository;
import fr.cph.stock.repository.PortfolioRepository;
import fr.cph.stock.repository.UserRepository;
import fr.cph.stock.security.SecurityService;
import fr.cph.stock.service.CurrencyService;
import fr.cph.stock.service.IndexService;
import fr.cph.stock.util.mail.MailService;
import fr.cph.stock.service.UserService;
import fr.cph.stock.util.Constants;
import fr.cph.stock.util.Mail;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
@Log4j2
public class UserServiceImpl implements UserService {

	private static final MathContext MATHCONTEXT = MathContext.DECIMAL32;
	private static final int DB_PASSWORD_LIMIT = 64;

	@NonNull
	private final AppProperties appProperties;
	@NonNull
	private final CurrencyService currencyService;
	@NonNull
	private final SecurityService securityService;
	@NonNull
	private final IndexService indexService;
	@NonNull
	private final UserRepository userRepository;
	@NonNull
	private final PortfolioRepository portfolioRepository;
	@NonNull
	private final AccountRepository accountRepository;
	@NonNull
	private final MailService mailService;

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
		userRepository.insert(user);
		final StringBuilder body = new StringBuilder();
		final String check = securityService.encodeToSha256(login + saltHashed + cryptedPasswordSalt + email);
		body.append("Welcome to ")
			.append(appProperties.getName())
			.append(",\n\nPlease valid your account by clicking on that link:")
			.append(appProperties.getAddress())
			.append(appProperties.getFolder())
			.append("/check?&login=")
			.append(login)
			.append("&check=")
			.append(check)
			.append(".\n\nBest regards,\nThe ")
			.append(appProperties.getName())
			.append(" team.");
		mailService.sendMail("[Registration] " + appProperties.getName(), body.toString(), new String[]{email});
		createUserPortfolio(user.getLogin());
		createUserDefaultAccount(user);
	}

	@Override
	public final Optional<User> getUser(final String login) {
		return userRepository.selectWithLogin(login);
	}

	@Override
	public final Optional<User> getUserWithEmail(final String email) {
		return userRepository.selectWithEmail(email);
	}

	@Override
	public final void validateUser(final String login) {
		final User user = userRepository.selectWithLogin(login).orElseThrow(() -> new NotFoundException(login));
		user.setAllow(true);
		userRepository.update(user);
	}

	@Override
	public final void updateUser(final User user) {
		userRepository.update(user);
	}

	/**
	 * @param login the login
	 */
	private void createUserPortfolio(final String login) {
		final User user = userRepository.selectWithLogin(login).orElseThrow(() -> new NotFoundException(login));
		final Portfolio portfolio = new Portfolio();
		portfolio.setCurrency(Currency.EUR);
		portfolio.setUserId(user.getId());
		portfolioRepository.insert(portfolio);
	}

	/**
	 * @param u the user
	 */
	private void createUserDefaultAccount(final User u) {
		final User user = userRepository.selectWithLogin(u.getLogin()).orElseThrow(() -> new NotFoundException(u.getLogin()));
		final Account account = Account.builder()
			.currency(Currency.EUR)
			.liquidity(0.0)
			.name("Default")
			.userId(user.getId())
			.del(false).build();
		accountRepository.insert(account);
	}

	@Override
	public final Optional<User> checkUser(final String login, final String md5Password) throws LoginException {
		Optional<User> userOptional = userRepository.selectWithLogin(login);
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
		userRepository.updateOneUserPassword(user);
	}

	@Override
	public final Optional<Portfolio> getUserPortfolio(final int userId, final Date from, final Date to) throws YahooException {
		final Optional<Portfolio> portfolioOptional = portfolioRepository.selectPortfolioFromUserIdWithEquities(userId, from, to);
		portfolioOptional.ifPresent(portfolio -> {
			Collections.sort(portfolio.getEquities());
			portfolio.setCurrency(currencyService.loadCurrencyData(portfolio.getCurrency()));
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

			if (!portfolio.getShareValues().isEmpty()) {
				final Date date = portfolio.getShareValues().get(portfolio.getShareValues().size() - 1).getDate();
				final List<Index> indexesCAC40 = indexService.getIndexes(Constants.CAC_40, date);
				final List<Index> indexesSP500 = indexService.getIndexes(Constants.SP_500, date);
				portfolio.addIndexes(indexesCAC40);
				portfolio.addIndexes(indexesSP500);
			}
		});
		return portfolioOptional;
	}

	@Override
	public Optional<Portfolio> getUserPortfolio(int userId, Date from) throws YahooException {
		return getUserPortfolio(userId, from, null);
	}

	@Override
	public final Optional<Portfolio> getUserPortfolio(final int userId) throws YahooException {
		return getUserPortfolio(userId, null, null);
	}

	@Override
	public final void updatePortfolio(final Portfolio portfolio) {
		portfolioRepository.update(portfolio);
	}

	@Override
	public final void updateLiquidity(final Account account, final double liquidity) {
		account.setLiquidity(liquidity);
		accountRepository.update(account);
	}
}
