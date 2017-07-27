package fr.cph.stock.controller.user;

import fr.cph.stock.config.AppProperties;
import fr.cph.stock.entities.User;
import fr.cph.stock.security.SecurityService;
import fr.cph.stock.util.mail.MailService;
import fr.cph.stock.service.UserService;
import fr.cph.stock.util.Mail;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static fr.cph.stock.util.Constants.EMAIL;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
@Controller
public class UserController {

	@NonNull
	private final AppProperties appProperties;
	@NonNull
	private final UserService userService;
	@NonNull
	private final SecurityService securityService;
	@NonNull
	private final MailService mailService;

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
				.append(appProperties.getAddress())
				.append(appProperties.getFolder())
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
}
