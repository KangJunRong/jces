package com.ecp.jces.server.util;

import com.ecp.jces.server.config.EmailConfig;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @date 2018/12/3.
 */
public class MailUtil {

	private static void sendMail(String host,String port, String user, String password, String to, String subject, String content) throws Exception{
		Properties prop = new Properties();
		prop.setProperty("mail.host", host);
		prop.setProperty("mail.smtp.port", port);
		prop.setProperty("mail.transport.protocol", "smtp");
		prop.setProperty("mail.smtp.auth", "true");

		Session session = Session.getInstance(prop, new Authenticator() {
			// 设置认证账户信息
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});

		session.setDebug(false);

		Transport ts = session.getTransport();
		//使用邮箱的用户名和密码连上邮件服务器
		ts.connect(host, user, password);
		//创建邮件
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(user));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);
		message.setContent(content, "text/html;charset=UTF-8");
		ts.sendMessage(message, message.getAllRecipients());
		ts.close();
	}

	/**
	 *
	 * @param to
	 * @param subject
	 * @param content
	 * @throws Exception
	 */
	public static void sendMail(String to, String subject, String content) throws Exception{
		sendMail(EmailConfig.MAIL_HOST, EmailConfig.MAIL_PORT, EmailConfig.MAIL_USER, EmailConfig.MAIL_PASSWORD, to, subject, content);

	}
}
