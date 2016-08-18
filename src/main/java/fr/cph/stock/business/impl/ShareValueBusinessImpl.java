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
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.util.Info;
import fr.cph.stock.util.Mail;
import fr.cph.stock.util.Util;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public enum ShareValueBusinessImpl implements ShareValueBusiness {

	INSTANCE;

	private static final Logger LOG = Logger.getLogger(BusinessImpl.class);
	private static final MathContext MATHCONTEXT = MathContext.DECIMAL32;
	private static final int PERCENT = 100;

	private final ShareValueDAO shareValueDAO;
	private final UserDAO userDAO;

	private final CompanyBusiness companyBusiness;
	private final UserBusiness userBusiness;

	ShareValueBusinessImpl() {
		shareValueDAO = new ShareValueDAO();
		userDAO = new UserDAO();
		companyBusiness = CompanyBusinessImpl.INSTANCE;
		userBusiness = UserBusinessImpl.INSTANCE;
	}

	@Override
	public final void updateCurrentShareValue(final Portfolio portfolio, final Account account, final Double liquidityMovement, final Double yield, final Double buy, final Double sell, final Double taxe, final String commentary) {
		final ShareValue shareValue = new ShareValue();
		shareValue.setUserId(portfolio.getUserId());
		final double monthlyYield = new BigDecimal(portfolio.getYieldYear() / 12, MATHCONTEXT).doubleValue();
		shareValue.setMonthlyYield(monthlyYield);
		shareValue.setPortfolioValue(new BigDecimal(portfolio.getTotalValue(), MATHCONTEXT).doubleValue());
		shareValue.setLiquidityMovement(liquidityMovement);
		shareValue.setYield(yield);
		shareValue.setBuy(buy);
		shareValue.setSell(sell);
		shareValue.setTaxe(taxe);
		shareValue.setAccount(account);
		// shareValue.setAccountName(account.getName());
		shareValue.setCommentary(commentary);
		shareValue.setDetails(portfolio.getPortfolioReview());
		ShareValue lastShareValue = shareValueDAO.selectLastValue(portfolio.getUserId());
		if (lastShareValue == null) {
			shareValue.setShareQuantity(portfolio.getTotalValue() / PERCENT);
			shareValue.setShareValue((double) PERCENT);
			shareValueDAO.insert(shareValue);
		} else {
			double parity;
			if (portfolio.getCurrency() == account.getCurrency()) {
				parity = 1;
			} else {
				parity = portfolio.getCurrency().getParity(account.getCurrency());
			}
			final Double quantity = lastShareValue.getShareQuantity() + (liquidityMovement * parity)
				/ ((portfolio.getTotalValue() - liquidityMovement * parity) / lastShareValue.getShareQuantity());
			shareValue.setShareQuantity(new BigDecimal(quantity, MATHCONTEXT).doubleValue());

			final Double shareValue2 = portfolio.getTotalValue() / quantity;
			shareValue.setShareValue(new BigDecimal(shareValue2, MATHCONTEXT).doubleValue());
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
		boolean tryToUpdate = false, canUpdate = false;
		final List<User> users = userDAO.selectAllUsers();
		Portfolio portfolio;
		Account account;
		for (final User user : users) {
			if (user.getUpdateHourTime() != null) {
				final int hourDiff = Util.timeZoneDiff(TimeZone.getTimeZone(user.getTimeZone()));
				final int hour = Util.getRealHour(user.getUpdateHourTime(), hourDiff);

				/*
				 * if (user.getLogin().equals("carl") || user.getLogin().equals("carlphilipp")) { LOG.info("========================");
				 * LOG.info("User : " + user.getLogin()); LOG.info("Current paris hour: " + calendar.get(Calendar.HOUR_OF_DAY));
				 * LOG.info("User current time zone: " + user.getTimeZone()); LOG.info("Hour diff: " + hourDiff); LOG.info("User wants to update at "
				 * + user.getUpdateHourTime()); LOG.info("Hour retained for user: " + hour); }
				 */

				final int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
				if (hour == currentHour) {
					if (!tryToUpdate) {
						canUpdate = companyBusiness.updateAllCompanies();
						tryToUpdate = true;
					}
					if (canUpdate) {
						LOG.info("Update user portfolio: " + user.getLogin());
						portfolio = userBusiness.getUserPortfolio(user.getId(), null, null);
						account = portfolio.getFirstAccount();
						updateCurrentShareValue(portfolio, account, 0.0, 0.0, 0.0, 0.0, 0.0, "Auto update");
					} else {
						if (user.getUpdateSendMail()) {
							final String body = ("Dear "
								+ user.getLogin()
								+ ",\n\nThe update today did not work, probably because of Yahoo's API.\nSorry for the inconvenience. You still can try do it manually."
								+ "\n\nBest regards,\nThe " + Info.NAME + " team.");
							Mail.sendMail("[Auto-update fail] " + Info.NAME, body, new String[]{user.getEmail()}, null);
						}
					}
				}
			}
		}
	}

	@Override
	public final ShareValue selectOneShareValue(final int id) {
		return shareValueDAO.select(id);
	}

	@Override
	public final void updateCommentaryShareValue(final ShareValue shareValue) {
		shareValueDAO.update(shareValue);
	}
}
