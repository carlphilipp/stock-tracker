package fr.cph.stock.controller;

import fr.cph.stock.exception.LoginException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.boot.context.config.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

	private static final String HEADER_FORWARDED_FOR = "X-FORWARDED-FOR";

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = LoginException.class)
	public String handleLoginException(final LoginException loginException) {
		log.warn("[{}] failed at login", loginException.getMessage());
		return "loginError";
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public String requestHandlingNoHandlerFound() {
		return "error";
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({HttpRequestMethodNotSupportedException.class, HttpSessionRequiredException.class})
	public String handleRequestError(final HttpServletRequest req, final Exception e) {
		final String ipAddress = req.getHeader(HEADER_FORWARDED_FOR) == null
			? req.getRemoteAddr()
			: req.getHeader(HEADER_FORWARDED_FOR);
		log.warn("Operation [{}] url [{}] ip [{}] resulted in [{}]", req.getMethod(), req.getRequestURL(), ipAddress, e.getClass().getSimpleName());
		return "error";
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(ResourceNotFoundException.class)
	public String handleResourceNotFoundException() {
		log.error("Resource not found");
		return "error";
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = Exception.class)
	public String handleException(final Exception exception) {
		log.error(exception.getMessage(), exception);
		return "error";
	}

	/**
	 * Force globally to convert request param to null when empty string
	 *
	 * @param binder
	 */
	@InitBinder
	public void binder(final WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
}
