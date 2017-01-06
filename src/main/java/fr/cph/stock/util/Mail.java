/**
 * Copyright 2017 Carl-Philipp Harmant
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.cph.stock.util;

import com.sun.net.ssl.internal.ssl.Provider;
import lombok.extern.log4j.Log4j2;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

/**
 * This class is used to send emails
 *
 * @author Carl-Philipp Harmant
 */
@Log4j2
public final class Mail {

	private static String smtpHostName;
	private static String smtpPort;
	private static String emailFromUsername;
	private static String emailFrom;
	private static String passwordFrom;
	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	/**
	 * Constructor
	 *
	 * @param emailSubjectTxt the subject
	 * @param emailMsgTxt     the content
	 * @param sendTo          the targets
	 * @param attachFile      the files to attach
	 * @throws MessagingException the messaging exception
	 * @throws IOException        the io exception
	 */
	private Mail(final String emailSubjectTxt, final String emailMsgTxt, final String[] sendTo, final String attachFile) throws MessagingException, IOException {
		final Properties prop = Util.getProperties();
		smtpHostName = prop.getProperty("email.smtp_host_name");
		smtpPort = prop.getProperty("email.smtp_port");
		emailFromUsername = prop.getProperty("email.from.username");
		emailFrom = prop.getProperty("email.from");
		passwordFrom = prop.getProperty("email.password");
		Security.addProvider(new Provider());
		sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt, attachFile);
	}

	/**
	 * Send the SSL message
	 *
	 * @param recipients tab of recipients
	 * @param subject    the subject
	 * @param message    the content
	 * @param attachFile the attach files
	 * @throws MessagingException the messaging exception
	 * @throws IOException        the io exception
	 */
	private void sendSSLMessage(final String[] recipients, final String subject, final String message, final String attachFile) throws MessagingException {
		final boolean debug = false;

		final Properties props = new Properties();
		props.put("mail.smtp.host", smtpHostName);
		props.put("mail.smtp.auth", "true");
		//props.put("mail.debug", "true");
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.socketFactory.port", smtpPort);
		props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.put("mail.smtp.ssl.enable", true);

		final Session session = Session.getDefaultInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				// First argument must be the email in case of querying Gmail
				// Must be only the username if querying Yahoo.
				return new PasswordAuthentication(emailFromUsername, passwordFrom);
			}
		});

		session.setDebug(debug);

		final Message msg = new MimeMessage(session);
		final InternetAddress addressFrom = new InternetAddress(emailFrom);
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
		if (attachFile != null) {
			final File file = new File(attachFile);
			final FileDataSource fds = new FileDataSource(file);
			final DataHandler dh = new DataHandler(fds);
			msg.setDataHandler(dh);
			msg.setFileName(attachFile);
		}
		Transport.send(msg);
	}

	/**
	 * Static access to send a mail
	 *
	 * @param emailSubjectTxt the email subject
	 * @param emailMsgTxt     the email content
	 * @param sendTo          the recipients
	 * @param attachFile      the attach files
	 */
	public static void sendMail(final String emailSubjectTxt, final String emailMsgTxt, final String[] sendTo, final String attachFile) {
		try {
			new Mail(emailSubjectTxt, emailMsgTxt, sendTo, attachFile);
		} catch (final MessagingException | IOException e1) {
			log.error("Error while trying to send an email : {}", e1.getMessage(), e1);
		}
	}
}
