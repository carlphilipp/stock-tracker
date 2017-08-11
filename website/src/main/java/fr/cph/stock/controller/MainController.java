package fr.cph.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class MainController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("index");
	}
}
