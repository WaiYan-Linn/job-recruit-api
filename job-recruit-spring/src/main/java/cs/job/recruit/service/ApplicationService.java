package cs.job.recruit.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cs.job.recruit.domain.entity.Application;
import cs.job.recruit.domain.entity.Job;
import cs.job.recruit.domain.entity.JobSeeker;
import cs.job.recruit.domain.repository.ApplicationRepository;
import cs.job.recruit.domain.repository.JobRepository;
import cs.job.recruit.domain.repository.JobSeekerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {

 private final ApplicationRepository applicationRepository;
 private final JobRepository jobRepository;
 private final JobSeekerRepository jobSeekerRepository;
 private final ResumeService resumeService;

 /**
  * Applies to a job as the given job seeker, uploading their resume in the same transaction.
  * @param accountIdStr UUID string of the Account/JobSeeker
  * @param jobId        the Job to apply to
  * @param file         the uploaded CV file
  */
 @Transactional
 public void applyWithResume(String accountIdStr, Long jobId, MultipartFile file) {
     UUID seekerId = UUID.fromString(accountIdStr);

     JobSeeker seeker = jobSeekerRepository
             .findByAccountId(seekerId)
             .orElseThrow(() -> new IllegalStateException("JobSeeker not found"));
     

     Job job = jobRepository.findById(jobId)
             .orElseThrow(() -> new IllegalArgumentException("Job not found"));
     
     // Prevent duplicate applications
     boolean exists = applicationRepository
             .existsByJobSeekerAndJob(seeker, job);
     if (exists) {
         throw new IllegalStateException("Already applied to this job");
     }

     // Store resume and update seeker.resumeUrl
     String filename;
     try {
         filename = resumeService.storeResume(accountIdStr, file);
     } catch (IOException e) {
         throw new RuntimeException("Failed to store resume", e);
     }

     // Create application
     Application app = new Application();
     app.setJobSeeker(seeker);
     app.setJob(job);
     app.setResumeUrl(filename);
     applicationRepository.save(app);

     // Optionally, record a status history entry here
 }

}
