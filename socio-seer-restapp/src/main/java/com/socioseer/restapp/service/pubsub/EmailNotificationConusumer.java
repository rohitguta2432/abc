package com.socioseer.restapp.service.pubsub;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.socioseer.common.domain.EMAIL_TYPE;
import com.socioseer.common.domain.EmailNotification;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.service.email.EmailService;
import com.socioseer.restapp.util.JsonParser;

@Slf4j
@Component
public class EmailNotificationConusumer {

  @Autowired
  private EmailService emailService;

  @KafkaListener(id = "email-notification-conusmer",
      containerFactory = "kafkaListenerContainerFactory",
      topics = "${kafka.topic.emailNotification}")
  public void consumer(ConsumerRecord<String, String> record) {
    EmailNotification emailNotification = parseEmailNotification(record.value());
    Map<String, Object> context = new HashMap<>();
    context.put("message", emailNotification.getMessage());
    emailService.sendEmail(EMAIL_TYPE.FORGOT_PASSWORD, context, emailNotification.getToList());
  }

  private EmailNotification parseEmailNotification(String notificationAsString) {
    try {
      return JsonParser.getObject(notificationAsString, EmailNotification.class);
    } catch (Exception e) {
      log.error("Error while parsing email notification object.");
      throw new SocioSeerException("Error while parsing email notification object.");
    }
  }

}
