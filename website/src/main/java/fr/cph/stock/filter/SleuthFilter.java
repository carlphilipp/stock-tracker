package fr.cph.stock.filter;

import fr.cph.stock.entities.User;
import org.apache.logging.log4j.CloseableThreadContext;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.instrument.web.TraceFilter;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static fr.cph.stock.util.Constants.USER;

public class SleuthFilter extends GenericFilterBean {

	private static final String TRACE_REQUEST_ATTR = TraceFilter.class.getName() + ".TRACE";

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		final Span currentSpan = (Span) request.getAttribute(TRACE_REQUEST_ATTR);
		final HttpSession httpSession = ((HttpServletRequest) request).getSession(false);
		final User user = httpSession == null ? null : (User) httpSession.getAttribute(USER);
		try (final CloseableThreadContext.Instance ignored = CloseableThreadContext.put("uuid", user == null ? null : user.getLogin() + "-" + currentSpan.getTraceId() + "-" + currentSpan.getSpanId())) {
			chain.doFilter(request, response);
		}
	}
}
