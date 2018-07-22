package com.smallsoup.csdn.service.model;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.google.common.collect.ImmutableList;

public class Email {

	// private Map<String, String> myAccount =
	// ImmutableMap.of("smallsoup@outlook.com", "Idojg0418");
	private String myAccount = "smallsoup@outlook.com";
	private String myAccountPwd = "Idojg0418";

	private List<String> toList;
	private List<String> ccList;
	private List<String> bccList = ImmutableList.of("ll837448792@163.com", "837448792@qq.com");

	private String subject;
	private Multipart content;
	private Date sendDate = new Date();

	public String getMyAccount() {
		return myAccount;
	}

	public void setMyAccount(String myAccount) {
		this.myAccount = myAccount;
	}

	public String getMyAccountPwd() {
		return myAccountPwd;
	}

	public void setMyAccountPwd(String myAccountPwd) {
		this.myAccountPwd = myAccountPwd;
	}

	public List<String> getToList() {
		return toList;
	}

	public void setToList(List<String> toList) {
		this.toList = toList;
	}

	public List<String> getCcList() {
		return ccList;
	}

	public void setCcList(List<String> ccList) {
		this.ccList = ccList;
	}

	public List<String> getBccList() {
		return bccList;
	}

	public void setBccList(List<String> bccList) {
		this.bccList = bccList;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Multipart getContent() {
		return content;
	}

	public void setContent(Multipart content) {
		this.content = content;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public Email(List<String> toList, File file) {
		this.toList = toList;
		this.subject = file.getName() + " yourFileArrived";
		MimeBodyPart text = new MimeBodyPart();
		try {
			text.setContent("让客官久等了，小店新开，还请多多支持，如果满意，希望下次的合作，新年快乐哦！", "text/html;charset=UTF-8");
			MimeBodyPart attachment = new MimeBodyPart();
			MimeMultipart mm = new MimeMultipart();
			if (file.exists()) {
				DataHandler dh2 = new DataHandler(new FileDataSource(file));
				attachment.setDataHandler(dh2);
				attachment.setFileName(MimeUtility.encodeText(dh2.getName()));
				mm.addBodyPart(attachment);
			}
			mm.addBodyPart(text);
			this.content = mm;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Email [myAccount=");
		builder.append(myAccount);
		builder.append(", myAccountPwd=");
		builder.append(myAccountPwd);
		builder.append(", toList=");
		builder.append(toList);
		builder.append(", ccList=");
		builder.append(ccList);
		builder.append(", bccList=");
		builder.append(bccList);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", content=");
		builder.append(content);
		builder.append(", sendDate=");
		builder.append(sendDate);
		builder.append("]");
		return builder.toString();
	}
}
