package com.example.dharam.services;


import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
public boolean sendEmail(String subject, String message, String to) {
		
		boolean flag=false;
		String from ="d2singh437@gmail.com";
		//variable for gmailhost
		String host="smtp.gmail.com";
		Properties properties = System.getProperties();
		//setting important information to properties object
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "587"); //google port 465 commonly
		properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
		properties.put("mail.smtp.starttls.enable", "true"); 
		properties.put("mail.smtp.auth", "true"); 
		
		//get session object
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("d2singh437@gmail.com","yyraydwdcersbmhu");
			}
		
		});
		session.setDebug(true);
		
		//compose email message
		try {
			
			//set message content
			MimeMessage mm = new MimeMessage(session);
			mm.setFrom(from);
			mm.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			mm.setSubject(subject);
			//mm.setText(message);
			mm.setContent(message, "text/html");
			
			//send message
			Transport.send(mm);
			flag=true;
			System.out.println("message sent successfully");
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return flag;
	}
}
