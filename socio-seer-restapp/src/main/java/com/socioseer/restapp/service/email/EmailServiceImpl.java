package com.socioseer.restapp.service.email;

import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.socioseer.common.domain.EMAIL_TYPE;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private EmailTemplateResolver templateResolver;

  @Override
  public void sendEmail(EMAIL_TYPE type, Object context, String recipientEmail) {
    this.sendEmail(type, context, Arrays.asList(recipientEmail));
  }

  @Override
  public void sendEmail(EMAIL_TYPE emailType, Object context, List<String> emails) {
    String message = templateResolver.resolve(emailType, context);
    log.debug("Sending notification of type {}  to {}", emailType, emails);
    try {
      log.debug("Sending {} to {}", emailType.name(), emails);
      MimeMessageHelper mimeMessage = this.getMimeMessage(emails, message, emailType.name());
      mailSender.send(mimeMessage.getMimeMessage());

    } catch (MessagingException e) {
      log.error("Unable to send email message", e);
    }
  }

  private MimeMessageHelper getMimeMessage(List<String> emails, String content, String subject)
      throws MessagingException {
    MimeMessage mimeMessage = this.mailSender.createMimeMessage();
    MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
    message.setSubject(subject);
    message.setBcc(emails.toArray(new String[emails.size()]));
    message.setText(content, true);
    return message;
  }

}
