package fr.cph.stock.util.mail;

public interface MailService {

	void sendMail(String emailSubjectTxt, String emailMsgTxt, String[] sendTo);
}
