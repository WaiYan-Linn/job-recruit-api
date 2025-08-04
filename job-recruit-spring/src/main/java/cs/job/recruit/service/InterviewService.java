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
            interviewDateTime = LocalDateTime.parse(request.dateTime()); // ISO 8601 string expected
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format.");
        }

        // 3. Save interview
        Interview interview = new Interview();
        interview.setApplication(application);
        interview.setInterviewDateTime(interviewDateTime);
        interview.setLocation(request.location());
        interview.setNotes(request.notes());
        interviewRepository.save(interview);

        // 4. Send email to job seeker
        String jobSeekerEmail = application.getJobSeeker().getAccount().getEmail();
        String subject = "Interview Invitation for " + application.getJob().getTitle();
        String body = "Dear " + application.getJobSeeker().getPersonalName() + ",\n\n" +
                "You are invited to an interview.\n\n" +
                "Location: " + request.location() + "\n" +
                "Date & Time: " + request.dateTime() + "\n\n" +
                (request.notes() != null ? "Notes: " + request.notes() + "\n\n" : "") +
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


    public void reject(Long applicationId, String employerEmail) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        String jobOwnerEmail = application.getJob().getEmployer().getAccount().getEmail();
        if (!jobOwnerEmail.equals(employerEmail)) {
            throw new AccessDeniedException("You are not authorized to reject this application.");
        }

        String jobSeekerEmail = application.getJobSeeker().getAccount().getEmail();
        String subject = "Update on Your Application – " + application.getJob().getTitle();
        String body = "Dear " + application.getJobSeeker().getPersonalName() + ",\n\n" +
                "Thank you sincerely for taking the time to apply for the position of " + application.getJob().getTitle() +
                " at " + application.getJob().getEmployer().getCompanyName() + ".\n\n" +
                "We were truly impressed by your background and accomplishments. After careful consideration, we regret to inform you that we have decided to move forward with another candidate at this time.\n\n" +
                "This decision was not easy, and we want to emphasize how much we value your interest in our company. We warmly encourage you to apply for future opportunities that match your profile.\n\n" +
                "Wishing you continued success and fulfillment in your career journey.\n\n" +
                "Kind regards,\n" +
                application.getJob().getEmployer().getCompanyName() + " Recruitment Team";

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


    public void hired(Long applicationId, String employerEmail) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        String jobOwnerEmail = application.getJob().getEmployer().getAccount().getEmail();
        if (!jobOwnerEmail.equals(employerEmail)) {
            throw new AccessDeniedException("You are not authorized to hire this applicant.");
        }

        String jobSeekerEmail = application.getJobSeeker().getAccount().getEmail();
        String subject = "Congratulations – You're Hired for " + application.getJob().getTitle() + "!";
        String body = "Dear " + application.getJobSeeker().getPersonalName() + ",\n\n" +
                "We’re thrilled to extend this offer to join us as a " + application.getJob().getTitle() +
                " at " + application.getJob().getEmployer().getCompanyName() + ".\n\n" +
                "Your experience, skills, and attitude truly stood out, and we’re confident you will make a fantastic addition to our team.\n\n" +
                "We will be reaching out shortly with the formal offer letter and next steps. Please keep an eye on your inbox for further communication.\n\n" +
                "Once again, congratulations and welcome to the team — we’re excited to begin this journey together!\n\n" +
                "Warmest regards,\n" +
                application.getJob().getEmployer().getCompanyName() + " HR Team";


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


    public InterviewRequest getInterviewInfo(Long id) {
        return interviewRepository.findById(id)
            .map(data -> new InterviewRequest(
            	data.getInterviewDateTime().toString(),
                data.getLocation(),
                data.getNotes()
            ))
            .orElse(new InterviewRequest("", "", ""));    }




}
