package fr.cph.stock.config;

import fr.cph.stock.controller.mobile.SessionMobileFilter;
import fr.cph.stock.filter.CookieFilter;
import fr.cph.stock.filter.SessionFilter;
import fr.cph.stock.filter.SleuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@EnableWebMvc
@Configuration
@ComponentScan({"fr.cph.stock"})
public class WebConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("/");
	}

	@Bean
	public InternalResourceViewResolver viewResolver() {
		final InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/jsp/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}

	@Override
	public void addViewControllers(final ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("forward:/WEB-INF/jsp/index.jsp");
	}

	@Bean
	public FilterRegistrationBean uuidFilter() {
		final FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new SleuthFilter());
		registration.setOrder(1);
		registration.addUrlPatterns("/*");
		return registration;
	}

	@Bean
	public FilterRegistrationBean sessionFilter() {
		final FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new SessionFilter());
		registration.setOrder(2);
		registration.addUrlPatterns("/home/*", "/history/*", "/accounts/*", "/charts/*", "/performance/*", "/currencies/*", "/options/*", "/language/*");
		return registration;
	}

	@Bean
	public FilterRegistrationBean sessionMobileFilter() {
		final FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new SessionMobileFilter());
		registration.setOrder(4);
		registration.addUrlPatterns("/homemobile/*", "/logoutmobile/*", "/reloadportfoliomobile/*", "/updatesharevaluemobile/*");
		return registration;
	}

	@Bean
	public FilterRegistrationBean cookieFilter() {
		final FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new CookieFilter());
		registration.setOrder(3);
		registration.addUrlPatterns("/home/*");
		return registration;
	}

	@Bean
	public ErrorPageFilter errorPageFilter() {
		return new ErrorPageFilter();
	}

	@Bean
	public FilterRegistrationBean disableSpringBootErrorFilter(final ErrorPageFilter filter) {
		final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(filter);
		filterRegistrationBean.setEnabled(false);
		return filterRegistrationBean;
	}
}
