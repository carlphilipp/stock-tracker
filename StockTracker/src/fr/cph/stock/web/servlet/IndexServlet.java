package fr.cph.stock.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@WebServlet(name = "IndexServlet", urlPatterns = { "/index" })
public class IndexServlet extends HttpServlet {

	/** **/
	private static final long serialVersionUID = 1L;
	/** Logger **/
	private static final Logger LOG = Logger.getLogger(IndexServlet.class);

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		if (request.getServerName().equals("stocktracker.fr")) {
			response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			response.setHeader("Location", "http://www.stocktracker.fr");
		} else {
			request.getRequestDispatcher("index.jsp").forward(request, response);
		}
	}
}
