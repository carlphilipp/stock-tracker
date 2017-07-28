package fr.cph.stock.controller.user;

import fr.cph.stock.config.AppProperties;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.LoginException;
import fr.cph.stock.security.SecurityService;
import fr.cph.stock.service.UserService;
import fr.cph.stock.util.mail.MailService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.cph.stock.util.Constants.*;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
@Controller
public class UserController {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");

	@NonNull
	private final AppProperties appProperties;
	@NonNull
	private final UserService userService;
	@NonNull
	private final SecurityService securityService;
	@NonNull
	private final MailService mailService;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView register(final HttpServletRequest request, final HttpServletResponse response,
								 @RequestParam(value = LOGIN) final String login,
								 @RequestParam(value = PASSWORD) final String password,
								 @RequestParam(value = EMAIL) final String email) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		final ModelAndView model = new ModelAndView("register");
		if(!isValidEmailAddress(email)){
			model.setViewName("error");
		}else {
			model.setViewName("register");
			try {
				userService.createUser(login, password, email);
			} catch (final LoginException e) {
				model.addObject(ERROR, e.getMessage());
			}
			model.addObject("login", login);
		}
		return model;
	}

	private boolean isValidEmailAddress(final String email) {
		final Matcher m = EMAIL_PATTERN.matcher(email);
		return m.matches();
	}

	@RequestMapping(value = "/lost", method = RequestMethod.POST)
	public ModelAndView lost(@RequestParam(value = EMAIL) final String email) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		final ModelAndView model = new ModelAndView("index");
		final Optional<User> userOptional = userService.getUserWithEmail(email);
		if (userOptional.isPresent()) {
			final User user = userOptional.get();
			final StringBuilder body = new StringBuilder();
			final String check = securityService.encodeToSha256(user.getLogin() + user.getPassword() + user.getEmail());
			body.append("Dear ")
				.append(user.getLogin())
				.append(",\n\nSomeone is trying to reset your password. If it is not you, just ignore this email.\n")
				.append("If it's you, click on this link:  ")
				.append(appProperties.getProtocol())
				.append("://")
				.append(appProperties.getUrl())
				.append("/newpassword?&login=")
				.append(user.getLogin())
				.append("&check=")
				.append(check)
				.append(".\n\nBest regards,\nThe ")
				.append(appProperties.getName())
				.append(" team.");
			mailService.sendMail("[Password Reset] " + appProperties.getName(), body.toString(), new String[]{email});
		}
		model.addObject("ok", "Check your email!");
		return model;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(final HttpServletRequest request, final HttpServletResponse response) {
		final HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		return "index";
	}
}
