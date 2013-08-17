package fr.cph.stock.web.servlet;

import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import fr.cph.stock.exception.LanguageException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.util.Info;

/**
 * This servlet is called to access the about page
 * 
 * @author Carl-Philipp Harmant
 * 
 */
@WebServlet(name = "AboutServlet", urlPatterns = { "/about" })
public class AboutServlet extends HttpServlet {

	/** Serialization **/
	private static final long serialVersionUID = -4486451014926965195L;
	/** Logger **/
	private static final Logger LOG = Logger.getLogger(AboutServlet.class);
	/** Language **/
	private LanguageFactory language;

	@Override
	public final void init() throws ServletException {
		try {
			language = LanguageFactory.getInstance();
		} catch (LanguageException e) {
			LOG.error(e.getMessage(), e);
			throw new ServletException("Error: " + e.getMessage(), e);
		}
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			request.setAttribute("language", language.getLanguage(lang));
			request.setAttribute("appTitle", Info.NAME + " &bull;   About");
			request.getRequestDispatcher("jsp/about.jsp").forward(request, response);
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}
}
