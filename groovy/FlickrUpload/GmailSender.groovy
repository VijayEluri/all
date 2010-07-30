import java.io.File;
import java.util.Properties;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;

class GmailSender extends EmailSender
{
	private static String USER = "hutiejun@gmail.com";
	private static String PASS = "deqrpl";
	private static String HOST = "smtp.gmail.com";
	private static String PORT = "465";

	private Session session;

	public GmailSender()
	{
		Properties props = new Properties();
		props.put("mail.smtp.user", USER);
		props.put("mail.smtp.host", HOST);
		props.put("mail.smtp.port", PORT);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.port", PORT);
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		Authenticator auth = new PasswordAuth(USER, PASS);
		session = Session.getInstance(props, auth);
	}

	public void send(String subject, File attachment) throws Exception
	{
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(USER));
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress(TO));
		msg.setSubject(subject);

		Multipart multipart = new MimeMultipart();
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(attachment.getAbsolutePath());
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(attachment.getName());
		multipart.addBodyPart(messageBodyPart);

		msg.setContent(multipart);
		Transport.send(msg);
	}
}
