package fr.cph.stock.controller.mobile;

import fr.cph.stock.entities.User;
import fr.cph.stock.exception.LoginException;
import fr.cph.stock.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static fr.cph.stock.util.Constants.*;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class MobileController {

	private final UserService userService;

	@RequestMapping(value = "/authmobile", method = RequestMethod.GET)
	private String login(
		final HttpServletRequest request,
		final HttpServletResponse response,
		@RequestParam(value = LOGIN) final String login,
		@RequestParam(value = PASSWORD) final String password
	) throws LoginException, IOException {
		final Optional<User> userOptional = userService.checkUser(login, password);
		if (userOptional.isPresent()) {
			final User user = userOptional.get();
			if (!user.getAllow()) {
				return "{\"error\":\"User not allowed}\"}";
			} else {
				request.getSession().setAttribute(USER, userOptional.get());
				response.sendRedirect(HOMEMOBILE);
				return "";
			}
		} else {
			return "{\"error\":\"Login or password unknown\"}";
		}
	}
}
