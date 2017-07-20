package fr.cph.stock;

import fr.cph.stock.exception.LoginException;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.config.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = LoginException.class)
	public String handleLoginException() {
		return "loginError";
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({HttpRequestMethodNotSupportedException.class, HttpSessionRequiredException.class})
	public String handleRequestError() {
		log.error("Request Error");
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
}
