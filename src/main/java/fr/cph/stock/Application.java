package fr.cph.stock;

import fr.cph.stock.web.servlet.SessionFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public FilterRegistrationBean sessionFilter() {
		final FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new SessionFilter());
		registration.addUrlPatterns("/home/*", "/history/*");
		return registration;
	}
}
