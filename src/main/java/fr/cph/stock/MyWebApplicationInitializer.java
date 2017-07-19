package fr.cph.stock;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public class MyWebApplicationInitializer implements WebApplicationInitializer {
	@Override
	public void onStartup(ServletContext container) {
//		ServletRegistration.Dynamic registration = container.addServlet("hello", new DispatcherServlet());
//		registration.setLoadOnStartup(1);
//		registration.addMapping("/hello*");		ServletRegistration.Dynamic registration = container.addServlet("hello", new DispatcherServlet());
//		registration.setLoadOnStartup(1);
//		registration.addMapping("/hello*");

/*		XmlWebApplicationContext appContext = new XmlWebApplicationContext();
		ServletRegistration.Dynamic registration2 = container.addServlet("dispatcher", new DispatcherServlet(appContext));
		registration2.setLoadOnStartup(1);
		registration2.addMapping("*.request");*/
	}
}
