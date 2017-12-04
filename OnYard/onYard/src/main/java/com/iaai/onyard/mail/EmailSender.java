package com.iaai.onyard.mail;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import com.iaai.onyard.application.OnYard;

/**
 * Class that enables background sending of emails via SMTP.
 */
public class EmailSender extends Authenticator {

    /**
     * The hostname of the Exchange server.
     */
    private final String mMailhost;
    /**
     * The current mail session.
     */
    private final Session mSession;
    /**
     * Container that holds the body parts of the MIME message.
     */
    private final Multipart mMultipart;

    /**
     * Default constructor. Initializes session with IAA Exchange server.
     */
    public EmailSender()
    {
        mMailhost = OnYard.IAA_MAIL_SERVER;

        final Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mMailhost);
        props.put("mail.smtp.port", "25");
        props.setProperty("mail.smtp.quitwait", "false");

        mSession = Session.getDefaultInstance(props, this);

        mMultipart = new MimeMultipart();
    }

    /**
     * Sends email via SMTP from IAA Exchange Server.
     * 
     * @param subject The email subject.
     * @param body The email body.
     * @param sender The email from address.
     * @param recipients The email recipient(s), comma delimited.
     * @throws AddressException if address parse failed.
     * @throws MessagingException
     */
    public synchronized void sendMail(String subject, String body, String sender, String recipients)
            throws AddressException, MessagingException
            {
        final MimeMessage message = new MimeMessage(mSession);
        final DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
        message.setFrom(new InternetAddress(sender));
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        message.setDataHandler(handler);

        final BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(body);

        mMultipart.addBodyPart(messageBodyPart);

        message.setContent(mMultipart);
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));

        Transport.send(message);
            }

    /**
     * Add a file attachment to the email.
     * 
     * @param filename The name of the file to attach.
     * @throws MessagingException
     */
    public void addAttachment(String filename)
            throws MessagingException
            {
        final BodyPart messageBodyPart = new MimeBodyPart();
        final DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        mMultipart.addBodyPart(messageBodyPart);
            }
}
