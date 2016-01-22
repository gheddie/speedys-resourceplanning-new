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

import org.apache.log4j.Logger;

public class MailSender
{
    private static final Logger logger = Logger.getLogger(MailSender.class);

    private static final String SPEEDYS_FROM = "noreply@tri-speedys.de";

    public static void sendHtmlMail(String toAddress, String body, String subject, final String smtpUsername,
            final String smtpPassword, final String smtpHost, final String smtpPort) throws AddressException,
            MessagingException
    {
        logger.info("sending mail [subject:" + subject + "] to : " + toAddress);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        Session session = Session.getInstance(props, new javax.mail.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(SPEEDYS_FROM));
        msg.setSubject(subject);
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress, false));
        msg.setContent(body, "text/html; charset=utf-8");
        msg.setSentDate(new Date());
        Transport.send(msg);      
    }
}