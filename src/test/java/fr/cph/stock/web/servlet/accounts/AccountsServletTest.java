package fr.cph.stock.web.servlet.accounts;

import fr.cph.stock.business.AccountBusiness;
import fr.cph.stock.business.UserBusiness;
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

	private User user;

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
		user = new User();
		user.setId(1);
	}

	@Test
	public void testAccountServlet() throws ServletException, YahooException {
		when(request.getSession(false)).thenReturn(httpSession);
		when(httpSession.getAttribute(USER)).thenReturn(user);
		when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(LANGUAGE, "English")});
		when(request.getRequestDispatcher(isA(String.class))).thenReturn(requestDispatcher);
		when(userBusiness.getUserPortfolio(eq(1), isNull(Date.class), isNull(Date.class))).thenReturn(new Portfolio());

		accountsServlet.doPost(request, response);

		verify(userBusiness).getUserPortfolio(eq(1), isNull(Date.class), isNull(Date.class));
		verify(request).setAttribute(eq(LANGUAGE), isA(Map.class));
		verify(request).setAttribute(eq(PORTFOLIO), isA(Portfolio.class));
		verify(request).setAttribute(eq(APP_TITLE), isA(String.class));
	}

	@Test
	public void testAccountServletFail() throws ServletException, YahooException {
		exception.expect(ServletException.class);

		when(request.getSession(false)).thenThrow(new RuntimeException());

		accountsServlet.doPost(request, response);

		verify(userBusiness, never()).getUserPortfolio(eq(1), isNull(Date.class), isNull(Date.class));
		verify(request, never()).setAttribute(eq(LANGUAGE), isA(Map.class));
		verify(request, never()).setAttribute(eq(PORTFOLIO), isA(Portfolio.class));
		verify(request, never()).setAttribute(eq(APP_TITLE), isA(String.class));
	}
}
