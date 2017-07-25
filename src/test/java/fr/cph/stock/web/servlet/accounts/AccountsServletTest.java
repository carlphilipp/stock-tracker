package fr.cph.stock.web.servlet.accounts;

import fr.cph.stock.service.AccountService;
import fr.cph.stock.service.UserService;
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
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;

import static fr.cph.stock.util.Constants.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
	private AccountService accountService;
	@Mock
	private UserService userService;

	@InjectMocks
	private AccountsServlet accountsServlet;

	@Before
	public void init() {
		accountsServlet.init();
		accountsServlet.setAccountService(accountService);
		accountsServlet.setUserService(userService);
		final User user = User.builder().id(1).build();

		when(request.getSession(false)).thenReturn(httpSession);
		when(httpSession.getAttribute(USER)).thenReturn(user);
	}

	@Test
	public void testAccountServlet() throws ServletException, YahooException {
		when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(LANGUAGE, "English")});
		when(request.getRequestDispatcher(isA(String.class))).thenReturn(requestDispatcher);
		when(userService.getUserPortfolio(eq(1))).thenReturn(Optional.of(new Portfolio()));

		accountsServlet.doPost(request, response);

		verify(userService).getUserPortfolio(eq(1));
		verifyMainAttributes();
	}

	@Test
	public void testAccountServletFail() throws ServletException, YahooException {
		exception.expect(ServletException.class);

		when(request.getSession(false)).thenThrow(new RuntimeException());

		accountsServlet.doPost(request, response);

		verify(userService, never()).getUserPortfolio(eq(1), isNull(), isNull());
		verifyMainAttributes();
	}

	@Test
	public void testAccountServletAdd() throws ServletException, YahooException {
		when(request.getParameter(ADD)).thenReturn("add");
		when(request.getParameter(CURRENCY)).thenReturn("USD");
		when(request.getParameter(LIQUIDITY)).thenReturn("10.5");
		when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(LANGUAGE, "English")});
		when(request.getRequestDispatcher(isA(String.class))).thenReturn(requestDispatcher);
		when(userService.getUserPortfolio(eq(1))).thenReturn(Optional.of(new Portfolio()));

		accountsServlet.doPost(request, response);

		verify(userService).getUserPortfolio(eq(1));
		verify(accountService).addAccount(isA(Account.class));
		verify(request).setAttribute(eq(MESSAGE), eq(ADDED));
		verifyMainAttributes();
	}

	@Test
	public void testAccountServletModify() throws ServletException, YahooException {
		when(request.getParameter(MOD)).thenReturn("mod");
		when(request.getParameter(CURRENCY)).thenReturn("USD");
		when(request.getParameter(LIQUIDITY)).thenReturn("10.5");
		when(request.getParameter(ID)).thenReturn("1");
		when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(LANGUAGE, "English")});
		when(request.getRequestDispatcher(isA(String.class))).thenReturn(requestDispatcher);
		when(userService.getUserPortfolio(eq(1))).thenReturn(Optional.of(new Portfolio()));

		accountsServlet.doPost(request, response);

		verify(userService).getUserPortfolio(eq(1));
		verify(accountService).updateAccount(isA(Account.class));
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
		when(userService.getUserPortfolio(eq(1))).thenReturn(Optional.of(new Portfolio()));

		accountsServlet.doPost(request, response);

		verify(userService).getUserPortfolio(eq(1));
		verify(accountService).deleteAccount(isA(Account.class));
		verifyMainAttributes();
	}

	@Test
	public void testAccountServletDeleteFail() throws ServletException, YahooException {
		when(request.getParameter(DELETE)).thenReturn("delete");
		when(request.getParameter(ID)).thenReturn("1");
		when(request.getParameter(DELETE_2)).thenReturn("false");
		when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(LANGUAGE, "English")});
		when(request.getRequestDispatcher(isA(String.class))).thenReturn(requestDispatcher);
		when(userService.getUserPortfolio(eq(1))).thenReturn(Optional.of(new Portfolio()));

		accountsServlet.doPost(request, response);

		verify(userService).getUserPortfolio(eq(1));
		verify(accountService, never()).deleteAccount(isA(Account.class));
		verify(request).setAttribute(eq(ERROR), isA(String.class));
		verifyMainAttributes();
	}

	public void verifyMainAttributes() {
		verify(request).setAttribute(eq(LANGUAGE), isA(Map.class));
		verify(request).setAttribute(eq(PORTFOLIO), isA(Portfolio.class));
		verify(request).setAttribute(eq(APP_TITLE), isA(String.class));
	}
}
