package fr.cph.stock.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.Properties;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Configuration
public class DataSourceConfig {

	@NonNull
	public final AppProperties appProperties;

	@Bean
	public DriverManagerDataSource db() {
		final DriverManagerDataSource db = new DriverManagerDataSource();
		db.setDriverClassName(appProperties.getDb().getDriver());
		db.setUrl("jdbc:mysql://" + appProperties.getDb().getIp() + "/" + appProperties.getDb().getName());
		final Properties properties = new Properties();
		properties.setProperty("serverTimezone", "Europe/Paris");
		properties.setProperty("useSSL", "false");
		db.setConnectionProperties(properties);
		db.setUsername(appProperties.getDb().getName());
		db.setPassword(appProperties.getDb().getPassword());
		return db;
	}
}
