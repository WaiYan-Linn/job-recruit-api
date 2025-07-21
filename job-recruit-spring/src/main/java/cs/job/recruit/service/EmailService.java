package cs.job.recruit.service;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired private JavaMailSender mailSender;

    public void sendOTP(String toEmail, String otp) {
    	try {
    	 	  MimeMessage message = mailSender.createMimeMessage();
              MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(new InternetAddress("waiyanlin2015015@gmail.com", "Carrer Hub"));
            helper.setTo(toEmail);
            helper.setSubject("Your OTP Code");
            helper.setText("Your OTP Code is " + otp + ". DO NOT! share it to anyone.");
            mailSender.send(message);
    	 } catch (MessagingException | UnsupportedEncodingException e) {
             throw new RuntimeException("Failed to send interview email", e);
         }
   
    }
}