import java.io.File;
import java.util.Properties;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;

class CorpSender extends EmailSender
{
	private static String USER = 'corp\\huj2';
	private static String FROM = 'hu_jason@emc.com';
	private static String PASS = "Deqrpl@#\$";
	private static String HOST = "CORPUSMX80B.corp.emc.com";
	private static String PORT = "25";

	private Session session;

	public CorpSender()
	{
		Properties props = new Properties();
		props.put("mail.smtp.user", USER);
		props.put("mail.smtp.host", HOST);
		props.put("mail.smtp.port", PORT);
		props.put("mail.smtp.auth", "true");
		Authenticator auth = new PasswordAuth(USER, PASS);
		session = Session.getInstance(props, auth);
	}

	public void send(String subject, File attachment) throws Exception
	{
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(FROM));
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
