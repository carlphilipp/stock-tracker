package fr.cph.stock.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
@Controller
public class TimeoutController {

	@RequestMapping(value = "/timeout", method = RequestMethod.GET)
	public String timeout(
		final HttpServletRequest request,
		final HttpServletResponse response) {
		return "timeout";
	}
}
