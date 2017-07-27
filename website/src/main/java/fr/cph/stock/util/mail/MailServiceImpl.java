package fr.cph.stock.util.mail;

import com.sun.net.ssl.internal.ssl.Provider;
import fr.cph.stock.config.AppProperties;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

@Log4j2
@Component
public class MailServiceImpl implements MailService {

	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	@NonNull
	private final AppProperties appProperties;

	@Autowired
	public MailServiceImpl(final AppProperties appProperties) throws MessagingException {
		/*final Properties prop = Util.getProperties();
		smtpHostName = prop.getProperty("email.smtp_host_name");
		smtpPort = prop.getProperty("email.smtp_port");
		emailFromUsername = prop.getProperty("email.from.username");
		emailFrom = prop.getProperty("email.from");
		passwordFrom = prop.getProperty("email.password");*/
		this.appProperties = appProperties;
		Security.addProvider(new Provider());
		//sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt);
	}

	/**
	 * Send the SSL message
	 *
	 * @param recipients tab of recipients
	 * @param subject    the subject
	 * @param message    the content
	 * @throws MessagingException the messaging exception
	 */
	private void sendSSLMessage(final String[] recipients, final String subject, final String message) throws MessagingException {
		final boolean debug = false;

		final Properties props = new Properties();
		props.put("mail.smtp.host", appProperties.getEmail().getSmtp().getHost());
		props.put("mail.smtp.auth", "true");
		//props.put("mail.debug", "true");
		props.put("mail.smtp.port", appProperties.getEmail().getSmtp().getPort());
		props.put("mail.smtp.socketFactory.port", appProperties.getEmail().getSmtp().getPort());
		props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.put("mail.smtp.ssl.enable", true);

		final Session session = Session.getDefaultInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				// First argument must be the email in case of querying Gmail
				// Must be only the username if querying Yahoo.
				return new PasswordAuthentication(appProperties.getEmail().getFrom().getUsername(), appProperties.getEmail().getFrom().getPassword());
			}
		});

		session.setDebug(debug);

		final Message msg = new MimeMessage(session);
		final InternetAddress addressFrom = new InternetAddress(appProperties.getEmail().getFrom().getFrom());
		msg.setFrom(addressFrom);

		final InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		msg.setContent(message, "text/plain");
		msg.setSentDate(new Date());
		Transport.send(msg);
		log.debug("Sending email to [{}]", recipients[0]);
	}

	/**
	 * Static access to send a mail
	 *
	 * @param emailSubjectTxt the email subject
	 * @param emailMsgTxt     the email content
	 * @param sendTo          the recipients
	 */
	public void sendMail(final String emailSubjectTxt, final String emailMsgTxt, final String[] sendTo) {
		try {
			sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt);
		} catch (final MessagingException e) {
			log.error("Error while trying to send an email : {}", e.getMessage(), e);
		}
	}
}
