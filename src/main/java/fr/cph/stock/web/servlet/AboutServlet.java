package fr.cph.stock.web.servlet;

import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.util.Info;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static fr.cph.stock.util.Constants.APP_TITLE;
import static fr.cph.stock.util.Constants.LANGUAGE;

/**
 * This servlet is called to access the about page
 *
 * @author Carl-Philipp Harmant
 */
@Log4j2
@WebServlet(name = "AboutServlet", urlPatterns = {"/about"})
public class AboutServlet extends HttpServlet {

	private static final long serialVersionUID = -4486451014926965195L;

	private LanguageFactory language;

	@Override
	public final void init() throws ServletException {
		this.language = LanguageFactory.INSTANCE;
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			request.setAttribute(LANGUAGE, language.getLanguage(lang));
			request.setAttribute(APP_TITLE, Info.NAME + " &bull;   About");
			request.getRequestDispatcher("jsp/about.jsp").forward(request, response);
		} catch (final Throwable t) {
			log.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}
}
