package fr.cph.stock.filter;

import fr.cph.stock.util.Constants;
import lombok.NonNull;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fr.cph.stock.util.Constants.*;

public class CookieFilter extends GenericFilterBean {

	private static final int ONE_YEAR_COOKIE = 60 * 60 * 24 * 365;

	@NonNull
	private final List<String> defaultCookies;

	public CookieFilter() {
		defaultCookies = new ArrayList<>();
		defaultCookies.add(Constants.QUOTE);
		defaultCookies.add(Constants.CURRENCY);
		defaultCookies.add(Constants.PARITY);
		defaultCookies.add(Constants.STOP_LOSS);
		defaultCookies.add(Constants.OBJECTIVE);
		defaultCookies.add(Constants.YIELD_1);
		defaultCookies.add(Constants.YIELD_2);
		defaultCookies.add(Constants.AUTO_UPDATE);
	}

	@Override
	public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;
		final List<Cookie> cookies = Arrays.asList(request.getCookies());
		defaultCookies.stream()
			.filter(cookieName -> !containsCookie(cookies, cookieName))
			.forEach(cookieName -> addCookieToResponse(response, cookieName, CHECKED));
		if (!containsCookie(cookies, LANGUAGE)) {
			addCookieToResponse(response, LANGUAGE, ENGLISH);
			request.setAttribute(LANGUAGE, ENGLISH);
		}
		chain.doFilter(request, response);
	}

	private void addCookieToResponse(final HttpServletResponse response, final String name, final String value) {
		final Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(ONE_YEAR_COOKIE);
		response.addCookie(cookie);
	}

	private boolean containsCookie(@NonNull final List<Cookie> cookies, final String cookieName) {
		return cookies.stream().anyMatch(cookie -> cookie.getName().equals(cookieName));
	}
}
