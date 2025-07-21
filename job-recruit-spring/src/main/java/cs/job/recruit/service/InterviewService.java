package cs.job.recruit.service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import cs.job.recruit.api.input.InterviewRequest;
import cs.job.recruit.domain.entity.Application;
import cs.job.recruit.domain.entity.Interview;
import cs.job.recruit.domain.repository.ApplicationRepository;
import cs.job.recruit.domain.repository.InterviewRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final JavaMailSender javaMailSender;



    public void scheduleInterview(Long applicationId, InterviewRequest request, String employerEmail) {
        // 1. Validate application and employer
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        String jobOwnerEmail = application.getJob().getEmployer().getAccount().getEmail();
        if (!jobOwnerEmail.equals(employerEmail)) {
            throw new AccessDeniedException("You are not authorized to schedule an interview for this application.");
        }

        // 2. Parse date
        LocalDateTime interviewDateTime;
        try {
            interviewDateTime = LocalDateTime.parse(request.getDateTime()); // ISO 8601 string expected
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format.");
        }

        // 3. Save interview
        Interview interview = new Interview();
        interview.setApplication(application);
        interview.setInterviewDateTime(interviewDateTime);
        interview.setLocation(request.getLocation());
        interview.setNotes(request.getNotes());
        interviewRepository.save(interview);

        // 4. Send email to job seeker
        String jobSeekerEmail = application.getJobSeeker().getAccount().getEmail();
        String subject = "Interview Invitation for " + application.getJob().getTitle();
        String body = "Dear " + application.getJobSeeker().getPersonalName() + ",\n\n" +
                "You are invited to an interview.\n\n" +
                "Location: " + request.getLocation() + "\n" +
                "Date & Time: " + request.getDateTime() + "\n\n" +
                (request.getNotes() != null ? "Notes: " + request.getNotes() + "\n\n" : "") +
                "Please be on time.\n\nBest regards,\n" +
                application.getJob().getEmployer().getCompanyName();

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(new InternetAddress("waiyanlin2015015@gmail.com", "Carrer Hub"));
            helper.setTo(jobSeekerEmail);
            helper.setSubject(subject);
            helper.setText(body, false); // false = plain text

            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send interview email", e);
        }
    }

}
