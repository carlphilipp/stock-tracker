package fr.cph.stock.web.servlet.accounts;

import fr.cph.stock.business.AccountBusiness;
import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.YahooException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;

import static fr.cph.stock.util.Constants.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountsServletTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	@Mock
	private RequestDispatcher requestDispatcher;
	@Mock
	private HttpSession httpSession;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private AccountBusiness accountBusiness;
	@Mock
	private UserBusiness userBusiness;

	@InjectMocks
	private AccountsServlet accountsServlet;

	@Before
	public void init() {
		accountsServlet.init();
		accountsServlet.setAccountBusiness(accountBusiness);
		accountsServlet.setUserBusiness(userBusiness);
		final User user = new User();
		user.setId(1);

		when(request.getSession(false)).thenReturn(httpSession);
		when(httpSession.getAttribute(USER)).thenReturn(user);
	}

	@Test
	public void testAccountServlet() throws ServletException, YahooException {
		when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(LANGUAGE, "English")});
		when(request.getRequestDispatcher(isA(String.class))).thenReturn(requestDispatcher);
		when(userBusiness.getUserPortfolio(eq(1), isNull(Date.class), isNull(Date.class))).thenReturn(new Portfolio());

		accountsServlet.doPost(request, response);

		verify(userBusiness).getUserPortfolio(eq(1), isNull(Date.class), isNull(Date.class));
		verifyMainAttributes();
	}

	@Test
	public void testAccountServletFail() throws ServletException, YahooException {
		exception.expect(ServletException.class);

		when(request.getSession(false)).thenThrow(new RuntimeException());

		accountsServlet.doPost(request, response);

		verify(userBusiness, never()).getUserPortfolio(eq(1), isNull(Date.class), isNull(Date.class));
		verifyMainAttributes();
	}

	@Test
	public void testAccountServletAdd() throws ServletException, YahooException {
		when(request.getParameter(ADD)).thenReturn("add");
		when(request.getParameter(CURRENCY)).thenReturn("USD");
		when(request.getParameter(LIQUIDITY)).thenReturn("10.5");
		when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(LANGUAGE, "English")});
		when(request.getRequestDispatcher(isA(String.class))).thenReturn(requestDispatcher);
		when(userBusiness.getUserPortfolio(eq(1), isNull(Date.class), isNull(Date.class))).thenReturn(new Portfolio());

		accountsServlet.doPost(request, response);

		verify(userBusiness).getUserPortfolio(eq(1), isNull(Date.class), isNull(Date.class));
		verify(accountBusiness).addAccount(isA(Account.class));
		verify(request).setAttribute(eq(MESSAGE), eq(ADDED));
		verifyMainAttributes();
	}

	@Test
	public void testAccountServletModify() throws ServletException, YahooException {
		;
		when(request.getParameter(MOD)).thenReturn("mod");
		when(request.getParameter(CURRENCY)).thenReturn("USD");
		when(request.getParameter(LIQUIDITY)).thenReturn("10.5");
		when(request.getParameter(ID)).thenReturn("1");
		when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(LANGUAGE, "English")});
		when(request.getRequestDispatcher(isA(String.class))).thenReturn(requestDispatcher);
		when(userBusiness.getUserPortfolio(eq(1), isNull(Date.class), isNull(Date.class))).thenReturn(new Portfolio());

		accountsServlet.doPost(request, response);

		verify(userBusiness).getUserPortfolio(eq(1), isNull(Date.class), isNull(Date.class));
		verify(accountBusiness).updateAccount(isA(Account.class));
		verify(request).setAttribute(eq(MESSAGE), eq(MODIFIED_MESSAGE));
		verifyMainAttributes();
	}

	@Test
	public void testAccountServletDelete() throws ServletException, YahooException {
		when(request.getParameter(DELETE)).thenReturn("delete");
		when(request.getParameter(ID)).thenReturn("1");
		when(request.getParameter(DELETE_2)).thenReturn("true");
		when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(LANGUAGE, "English")});
		when(request.getRequestDispatcher(isA(String.class))).thenReturn(requestDispatcher);
		when(userBusiness.getUserPortfolio(eq(1), isNull(Date.class), isNull(Date.class))).thenReturn(new Portfolio());

		accountsServlet.doPost(request, response);

		verify(userBusiness).getUserPortfolio(eq(1), isNull(Date.class), isNull(Date.class));
		verify(accountBusiness).deleteAccount(isA(Account.class));
		verifyMainAttributes();
	}

	@Test
	public void testAccountServletDeleteFail() throws ServletException, YahooException {
		when(request.getParameter(DELETE)).thenReturn("delete");
		when(request.getParameter(ID)).thenReturn("1");
		when(request.getParameter(DELETE_2)).thenReturn("false");
		when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(LANGUAGE, "English")});
		when(request.getRequestDispatcher(isA(String.class))).thenReturn(requestDispatcher);
		when(userBusiness.getUserPortfolio(eq(1), isNull(Date.class), isNull(Date.class))).thenReturn(new Portfolio());

		accountsServlet.doPost(request, response);

		verify(userBusiness).getUserPortfolio(eq(1), isNull(Date.class), isNull(Date.class));
		verify(accountBusiness, never()).deleteAccount(isA(Account.class));
		verify(request).setAttribute(eq(ERROR), isA(String.class));
		verifyMainAttributes();
	}

	public void verifyMainAttributes() {
		verify(request).setAttribute(eq(LANGUAGE), isA(Map.class));
		verify(request).setAttribute(eq(PORTFOLIO), isA(Portfolio.class));
		verify(request).setAttribute(eq(APP_TITLE), isA(String.class));
	}
}
