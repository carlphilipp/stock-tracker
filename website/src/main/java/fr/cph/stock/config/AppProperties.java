package fr.cph.stock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
	private String name;
	private String address;
	private String folder;
	private List<String> admins;

	private final Db db = new Db();
	private final Dropbox dropbox = new Dropbox();
	private final Email email = new Email();
	private final Report report = new Report();

	@Data
	public static class Db {
		private String ip;
		private String driver;
		private String name;
		private String user;
		private String password;
	}

	@Data
	public static class Dropbox {
		private String clientId;
		private String accessToken;
	}

	@Data
	public static class Email {
		private final Smtp smtp = new Smtp();
		private final From from = new From();

		@Data
		public static class Smtp {
			private String host;
			private String port;
		}

		@Data
		public static class From {
			private String username;
			private String password;
			private String from;
		}
	}

	@Data
	public static class Report {
		private String ireport;
	}
}
