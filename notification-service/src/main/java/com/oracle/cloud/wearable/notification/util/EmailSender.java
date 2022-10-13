package com.oracle.cloud.wearable.notification.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
@Slf4j
public final class EmailSender {

  private static final String ENV_KEY_OCI_SMTP_USERNAME = "OCI_SMTP_USERNAME";
  private static final String ENV_KEY_OCI_SMTP_PASSWORD_OCID = "OCI_SMTP_PASSWORD_OCID";
  private static final String ENV_KEY_OCI_EMAIL_HOST = "OCI_EMAIL_HOST";
  private static final String ENV_KEY_OCI_EMAIL_PORT = "OCI_EMAIL_PORT";
  private static final String ENV_KEY_OCI_EMAIL_FROM_ADDRESS = "OCI_EMAIL_FROM_ADDRESS";
  private static final String ENV_KEY_OCI_EMAIL_FROM_NAME = "OCI_EMAIL_FROM_NAME";
  private static final String ENV_KEY_OCI_EMAIL_SUBJECT = "OCI_EMAIL_SUBJECT";

  private Session session;

  private SecretUtil secretUtil;

  @Autowired
  public EmailSender(final SecretUtil secretUtil) {
    final Properties props = System.getProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.port", Integer.parseInt(System.getenv().get(ENV_KEY_OCI_EMAIL_PORT)));

    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.auth.login.disable", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.starttls.required", "true");
    props.put("mail.smtp.ssl.protocols", "TLSv1.2");

    session = Session.getDefaultInstance(props);
    this.secretUtil = secretUtil;
  }

  public void sendEmail(final String emailBody, final String toAddress) {
    Transport transport = null;
    try {
      final MimeMessage msg = new MimeMessage(session);
      msg.setFrom(
          new InternetAddress(
              System.getenv().get(ENV_KEY_OCI_EMAIL_FROM_ADDRESS),
              System.getenv().get(ENV_KEY_OCI_EMAIL_FROM_NAME)));
      msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
      msg.setSubject(System.getenv().get(ENV_KEY_OCI_EMAIL_SUBJECT));
      msg.setContent(emailBody, "text/html");

      transport = session.getTransport();
      transport.connect(
          System.getenv().get(ENV_KEY_OCI_EMAIL_HOST),
          System.getenv().get(ENV_KEY_OCI_SMTP_USERNAME),
          secretUtil.getSecretValue(System.getenv().get(ENV_KEY_OCI_SMTP_PASSWORD_OCID)));
      transport.sendMessage(msg, msg.getAllRecipients());
    } catch (Exception e) {
      log.error("Exception occurred while sending email", e);
      throw new RuntimeException(e);
    } finally {
      if (transport != null) {
        try {
          transport.close();
        } catch (MessagingException e) {
          //
        }
      }
    }
  }
}