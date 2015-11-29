package de.trispeedys.resourceplanning.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender
{
    private static final String FROM = "from-email@gmail.com";

    public static void sendMail(String toAddress, String body, String subject) throws AddressException, MessagingException
    {
        final String username = "testhelper1.trispeedys@gmail.com";
        final String password = "trispeedys1234";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props, new javax.mail.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(username, password);
            }
        });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
        message.setSubject(subject);
        message.setText(body);
        Transport.send(message);
        System.out.println("Done");
    }

    public static void sendHtmlMail(String toAddress, String body, String subject) throws AddressException, MessagingException
    {
        final String username = "testhelper1.trispeedys@gmail.com";
        final String password = "trispeedys1234";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props, new javax.mail.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(username, password);
            }
        });
        Message msg = new MimeMessage(session);
        msg.setSubject(subject);
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress, false));
        msg.setContent(body, "text/html; charset=utf-8");
        msg.setSentDate(new Date());
        Transport.send(msg);
    }
}