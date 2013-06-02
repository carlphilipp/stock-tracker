/**
 * Copyright 2013 Carl-Philipp Harmant
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.cph.stock.util;

import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

/**
 * This class is used to send emails
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class Mail {

	/** The logger **/
	private static final Logger log = Logger.getLogger(Mail.class);
	/** Smtp host **/
	private static String SMTP_HOST_NAME;
	/** Smtp port **/
	private static String SMTP_PORT;
	/** Email of the sender **/
	private static String emailFrom;
	/** Password of the sender **/
	private static String passwordFrom;
	/** SSL factory **/
	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	/**
	 * Constructor
	 * 
	 * @param emailSubjectTxt
	 *            the subject
	 * @param emailMsgTxt
	 *            the content
	 * @param sendTo
	 *            the targets
	 * @param attachFile
	 *            the files to attach
	 * @throws MessagingException
	 * @throws IOException
	 */
	private Mail(String emailSubjectTxt, String emailMsgTxt, String[] sendTo, String attachFile) throws MessagingException,
			IOException {
		Properties prop = Util.getProperties("app.properties");
		SMTP_HOST_NAME = prop.getProperty("email.smtp_host_name");
		SMTP_PORT = prop.getProperty("email.smtp_port");
		emailFrom = prop.getProperty("email.from");
		passwordFrom = prop.getProperty("email.password");
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt, emailFrom, attachFile);
	}

	/**
	 * Send the SSL message
	 * 
	 * @param recipients
	 *            tab of recipients
	 * @param subject
	 *            the subject
	 * @param message
	 *            the content
	 * @param from
	 *            the sender
	 * @param attachFile
	 *            the attach files
	 * @throws MessagingException
	 * @throws IOException
	 */
	public void sendSSLMessage(String recipients[], String subject, String message, String from, String attachFile)
			throws MessagingException, IOException {
		boolean debug = false;

		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "false");
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.put("mail.smtp.socketFactory.fallback", "false");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailFrom, passwordFrom);
			}
		});

		session.setDebug(debug);

		Message msg = new MimeMessage(session);
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		msg.setContent(message, "text/plain");
		if (attachFile != null) {
			File file = new File(attachFile);
			FileDataSource fds = new FileDataSource(file);
			DataHandler dh = new DataHandler(fds);
			msg.setDataHandler(dh);
			msg.setFileName(attachFile);
		}
		Transport.send(msg);
	}

	/**
	 * Static access to send a mail
	 * 
	 * @param emailSubjectTxt
	 *            the email subject
	 * @param emailMsgTxt
	 *            the email content
	 * @param sendTo
	 *            the recipients
	 * @param attachFile
	 *            the attach files
	 */
	public static void sendMail(String emailSubjectTxt, String emailMsgTxt, String[] sendTo, String attachFile) {
		try {
			new Mail(emailSubjectTxt, emailMsgTxt, sendTo, attachFile);
		} catch (MessagingException | IOException e1) {
			log.error("Error while trying to send an email : " + e1.getMessage(), e1);
		}
	}
}