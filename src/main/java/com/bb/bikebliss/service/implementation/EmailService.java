package com.bb.bikebliss.service.implementation;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("unused")
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        try {
            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (MailException e) {
            log.error("Error sending email: {}", e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred when sending email: {}", e.getMessage(), e);
        }
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indică faptul că conținutul este HTML

            mailSender.send(mimeMessage);
            log.info("HTML email sent successfully to {}", to);
        } catch (MailException e) {
            log.error("Error sending HTML email: {}", e.getMessage());
        } catch (MessagingException e) {
            log.error("Error setting HTML content: {}", e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred when sending HTML email: {}", e.getMessage(), e);
        }
    }
}