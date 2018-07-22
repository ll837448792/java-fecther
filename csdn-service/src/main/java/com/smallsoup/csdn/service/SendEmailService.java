package com.smallsoup.csdn.service;

import java.io.File;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.smallsoup.csdn.service.model.Email;

@Component
public class SendEmailService {

	public static final String CLASS_PATH = SendEmailService.class.getClassLoader().getResource("").getPath();

	public boolean sendEmail(Email email) throws Exception {
		Properties properties = getProperties();
		Session session = Session.getInstance(properties);
		session.setDebug(false);
		Transport transport = null;
		try {
			MimeMessage message = createMimeMessage(session, email);
			transport = session.getTransport();
			transport.connect(email.getMyAccount(), email.getMyAccountPwd());
			transport.sendMessage(message, message.getAllRecipients());
		} finally {
			if (null != transport) {
				transport.close();
			}
		}
		return true;
	}

	public MimeMessage createMimeMessage(Session session, Email email) throws Exception {
		MimeMessage message = new MimeMessage(session);

		System.out.println("createMimeMessage .....");
		message.setFrom(new InternetAddress(email.getMyAccount(), "smallsoup", "UTF-8"));

		if (StringUtils.isNotEmpty(email.getToList().get(0))) {
			message.addRecipient(MimeMessage.RecipientType.TO,
					new InternetAddress(email.getToList().get(0), "soup", "UTF-8"));
		}
		message.addRecipient(MimeMessage.RecipientType.BCC,
				new InternetAddress(email.getBccList().get(0), "soup", "UTF-8"));
		message.addRecipient(MimeMessage.RecipientType.BCC,
				new InternetAddress(email.getBccList().get(1), "soup", "UTF-8"));

		message.setSubject(email.getSubject(), "UTF-8");

		message.setContent(email.getContent());

		message.setSentDate(email.getSendDate());

		message.saveChanges();

		return message;
	}

	public static void main(String[] args) {
		File file = new File("E:/ProgrammerRoute/TaoBao/119188886827619917——学生社团管理系统 附带源码-CSDN下载/bysj-master.zip");
		// File file = new
		// File("E:/ProgrammerRoute/TaoBao/104256071056741249——宇航概论ppt(
		// pdf格式)/宇航概论ppt( pdf格式).rar");
		try {
			// sendEmail(null, file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Properties getProperties() {
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.smtp.host", "smtp-mail.outlook.com");
		props.setProperty("mail.smtp.starttls.enable", "true");
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.smtp.socketFactory.port", "587");
		props.setProperty("mail.smtp.socketFactory.fallback", "true");
		return props;
	}
}
