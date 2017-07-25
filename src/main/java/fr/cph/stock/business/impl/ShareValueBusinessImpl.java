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

package fr.cph.stock.business.impl;

import fr.cph.stock.business.CompanyBusiness;
import fr.cph.stock.business.ShareValueBusiness;
import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.dao.ShareValueDAO;
import fr.cph.stock.dao.UserDAO;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.ShareValue;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.util.Info;
import fr.cph.stock.util.Mail;
import fr.cph.stock.util.Util;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
@Component
public class ShareValueBusinessImpl implements ShareValueBusiness {

	private static final MathContext MATHCONTEXT = MathContext.DECIMAL32;
	private static final int PERCENT = 100;

	@NonNull
	private final ShareValueDAO shareValueDAO;
	@NonNull
	private final UserDAO userDAO;
	@NonNull
	private final CompanyBusiness companyBusiness;
	@NonNull
	private final UserBusiness userBusiness;

	@Override
	public final void updateCurrentShareValue(final Portfolio portfolio, final Account account, final Double liquidityMovement, final Double yield, final Double buy, final Double sell, final Double tax, final String commentary) {
		final ShareValue shareValue = ShareValue.builder()
			.userId(portfolio.getUserId())
			.monthlyYield(new BigDecimal(portfolio.getYieldYear() / 12, MATHCONTEXT).doubleValue())
			.portfolioValue(new BigDecimal(portfolio.getTotalValue(), MATHCONTEXT).doubleValue())
			.liquidityMovement(liquidityMovement)
			.yield(yield)
			.buy(buy)
			.sell(sell)
			.taxe(tax)
			.account(account)
			.commentary(commentary)
			.details(portfolio.getPortfolioReview())
			.build();

		Optional<ShareValue> lastShareValue = shareValueDAO.selectLastValue(portfolio.getUserId());
		if (lastShareValue.isPresent()) {
			double parity = portfolio.getCurrency() == account.getCurrency()
				? 1
				: portfolio.getCurrency().getParity(account.getCurrency());
			final Double quantity = lastShareValue.get().getShareQuantity() + (liquidityMovement * parity)
				/ ((portfolio.getTotalValue() - liquidityMovement * parity) / lastShareValue.get().getShareQuantity());
			shareValue.setShareQuantity(new BigDecimal(Double.toString(quantity), MATHCONTEXT).doubleValue());

			final Double shareValue2 = portfolio.getTotalValue() / quantity;
			shareValue.setShareValue(new BigDecimal(Double.toString(shareValue2), MATHCONTEXT).doubleValue());
			shareValueDAO.insert(shareValue);
		} else {
			shareValue.setShareQuantity(portfolio.getTotalValue() / PERCENT);
			shareValue.setShareValue((double) PERCENT);
			shareValueDAO.insert(shareValue);
		}
	}

	@Override
	public final void deleteShareValue(final ShareValue sv) {
		shareValueDAO.delete(sv);
	}

	@Override
	public final void addShareValue(final ShareValue share) {
		shareValueDAO.insertWithDate(share);
	}

	@Override
	public final void autoUpdateUserShareValue(final Calendar calendar) throws YahooException {
		boolean tryToUpdate = false, companyUpdateSuccess = false;
		final List<User> users = userDAO.selectAllUsers();
		for (final User user : users) {
			if (user.getUpdateHourTime() != null) {
				final int hourDiff = Util.timeZoneDiff(TimeZone.getTimeZone(user.getTimeZone()));
				final int hour = Util.getRealHour(user.getUpdateHourTime(), hourDiff);
				final int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
				if (hour == currentHour) {
					if (!tryToUpdate) {
						companyUpdateSuccess = companyBusiness.updateAllCompanies();
						tryToUpdate = true;
					}
					if (companyUpdateSuccess) {
						log.info("Update user portfolio: {}", user.getLogin());
						final Optional<Portfolio> portfolioOptional = userBusiness.getUserPortfolio(user.getId());
						portfolioOptional.ifPresent(portfolio -> {
							final Account account = portfolio.getFirstAccount().orElseThrow(() -> new NotFoundException("Account not found"));
							updateCurrentShareValue(portfolio, account, 0.0, 0.0, 0.0, 0.0, 0.0, "Auto update");
						});
					} else {
						if (user.getUpdateSendMail()) {
							final String body = ("Dear "
								+ user.getLogin()
								+ ",\n\nThe update today did not work, probably because of Yahoo's API.\nSorry for the inconvenience. You still can try do it manually."
								+ "\n\nBest regards,\nThe " + Info.NAME + " team.");
							Mail.sendMail("[Auto-update fail] " + Info.NAME, body, new String[]{user.getEmail()});
						}
					}
				}
			}
		}
	}

	@Override
	public final Optional<ShareValue> selectOneShareValue(final int id) {
		return shareValueDAO.select(id);
	}

	@Override
	public final void updateCommentaryShareValue(final ShareValue shareValue) {
		shareValueDAO.update(shareValue);
	}
}
