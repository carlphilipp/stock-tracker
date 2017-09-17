package fr.cph.stock.controller.mobile;

import com.google.gson.JsonObject;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Index;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.LoginException;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.service.CompanyService;
import fr.cph.stock.service.IndexService;
import fr.cph.stock.service.ShareValueService;
import fr.cph.stock.service.UserService;
import fr.cph.stock.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static fr.cph.stock.util.Constants.*;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class MobileController {

	private final MathContext mathContext = MathContext.DECIMAL32;

	private final UserService userService;
	private final IndexService indexService;
	private final CompanyService companyService;
	private final ShareValueService shareValueService;

	@RequestMapping(value = "/authmobile", method = RequestMethod.GET)
	private String login(
		final HttpServletRequest request,
		final HttpServletResponse response,
		@RequestParam(value = LOGIN) final String login,
		@RequestParam(value = PASSWORD) final String password
	) throws LoginException, IOException {
		request.getSession().invalidate();
		final Optional<User> userOptional = userService.checkUser(login, password);
		if (userOptional.isPresent()) {
			final User user = userOptional.get();
			if (!user.getAllow()) {
				return "{\"error\":\"User not allowed}\"}";
			} else {
				request.getSession().setAttribute(USER, userOptional.get());
				response.sendRedirect(HOMEMOBILE);
				return "";
			}
		} else {
			return "{\"error\":\"Login or password unknown\"}";
		}
	}

	@RequestMapping(value = "/homemobile", method = RequestMethod.GET)
	private void home(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final HttpSession session = request.getSession(false);
			final User user = (User) session.getAttribute(USER);
			Portfolio portfolio = null;
			try {
				portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
				if (portfolio.getShareValues().size() != 0) {
					Date from = portfolio.getShareValues().get(portfolio.getShareValues().size() - 1).getDate();
					List<Index> indexes = indexService.getIndexes(Constants.CAC_40, from, null);
					List<Index> indexes2 = indexService.getIndexes(Constants.SP_500, from, null);
					portfolio.addIndexes(indexes);
					portfolio.addIndexes(indexes2);
				}
				// to force calculate some data .... :'(
				portfolio.getCurrentShareValuesTaxes();
			} catch (final YahooException e) {
				log.error("Error: {}", e.getMessage(), e);
			}

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");

			if (portfolio != null) {
				final JsonObject json = new JsonObject();
				json.add(PORTFOLIO, portfolio.getJSONObject());
				json.add(USER, user.getJSONObject());
				response.getWriter().write(json.toString());
			} else {
				response.getWriter().write("{\"error\":empty\"}");
			}
		} catch (final Throwable t) {
			log.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@RequestMapping(value = "/logoutmobile", method = RequestMethod.GET)
	private void logout(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			request.getSession().invalidate();
			final String login = request.getParameter(LOGIN);
			final String password = request.getParameter(PASSWORD);
			final Optional<User> userOptional = userService.checkUser(login, password);
			if (userOptional.isPresent()) {
				final User user = userOptional.get();
				if (!user.getAllow()) {
					response.setContentType("application/json");
					response.getWriter().write("{\"error\":\"User not allowed}\"}");
				} else {
					request.getSession().setAttribute(USER, userOptional);
					response.sendRedirect(HOMEMOBILE);
				}
			} else {
				response.setContentType("application/json");
				response.getWriter().write("{\"error\":\"Login or password unknown\"}");
			}
		} catch (final Throwable t) {
			log.error("Error: {}", t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@RequestMapping(value = "/reloadportfoliomobile", method = RequestMethod.GET)
	private void reloadPortfolio(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final HttpSession session = request.getSession(false);
			final User user = (User) session.getAttribute(Constants.USER);
			try {
				final Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
				companyService.addOrUpdateCompaniesLimitedRequest(portfolio.getCompaniesYahooIdRealTime());
				response.sendRedirect(Constants.HOMEMOBILE);
			} catch (YahooException e) {
				response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
			}
		} catch (final Throwable t) {
			log.error("Error: {}", t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@RequestMapping(value = "/updatesharevaluemobile", method = RequestMethod.GET)
	private void updateShareValue(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final HttpSession session = request.getSession(false);
			final User user = (User) session.getAttribute(Constants.USER);
			Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
			try {
				final int accountId = Integer.valueOf(request.getParameter(Constants.ACCOUNT_ID));
				final double movement = Double.valueOf(request.getParameter(Constants.LIQUIDITY));
				final double yield = Double.valueOf(request.getParameter(Constants.YIELD));
				final double buy = Double.valueOf(request.getParameter(Constants.BUY));
				final double sell = Double.valueOf(request.getParameter(Constants.SELL));
				final double taxe = Double.valueOf(request.getParameter(Constants.TAXE));
				final String commentary = request.getParameter(Constants.COMMENTARY);
				Account account = null;
				for (final Account acc : portfolio.getAccounts()) {
					if (acc.getId() == accountId) {
						account = acc;
						break;
					}
				}

				if (account == null) {
					response.getWriter().write("{\"error\":\"Account not found\"}");
				} else {
					double newLiquidity = account.getLiquidity() + movement + yield - buy + sell - taxe;
					newLiquidity = new BigDecimal(Double.toString(newLiquidity), mathContext).doubleValue();
					userService.updateLiquidity(account, newLiquidity);
					portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
					shareValueService.updateCurrentShareValue(portfolio, account, movement, yield, buy, sell, taxe, commentary);
					response.sendRedirect(Constants.HOMEMOBILE);
				}

				// service.updateOneCurrency(portfolio.getCurrency());
				// service.addOrUpdateCompaniesLimitedRequest(portfolio.getCompaniesYahooIdRealTime());

			} catch (final NumberFormatException e) {
				response.getWriter().write("{\"error\":" + e.getMessage() + "\"}");
			}
		} catch (final Throwable t) {
			log.error("Error: {}", t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}
}
