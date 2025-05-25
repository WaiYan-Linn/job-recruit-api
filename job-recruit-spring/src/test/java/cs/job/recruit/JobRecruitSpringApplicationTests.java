package cs.job.recruit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import cs.job.recruit.domain.entity.Application;
import cs.job.recruit.domain.entity.JobSeeker;
import cs.job.recruit.domain.repository.ApplicationRepository;
import cs.job.recruit.domain.repository.JobSeekerRepository;

	@SpringBootTest(
			  classes = JobRecruitSpringApplication.class   // <— point to your @SpringBootApplication
			)
	@AutoConfigureMockMvc(addFilters = false)  
	class JobRecruitSpringApplicationTests {

		@Autowired
	    private MockMvc mvc;

	    @Autowired
	    private JobSeekerRepository jobSeekerRepository;
	    
	    @Autowired
	    private ApplicationRepository applicationRepository;

	    private final UUID seekerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
	 // Exactly mirror what your service uses:
	    private static final Path UPLOAD_DIR = 
	        Paths.get("uploads", "resumes").toAbsolutePath();

	 

	    @Test
	    void applyToJob_uploadsAndSavesFilename() throws Exception {
	        // 1. Load a real PDF from test resources
	        byte[] fileBytes = Files.readAllBytes(Paths.get("src/test/resources/sample.pdf"));
	        MockMultipartFile file = new MockMultipartFile(
	            "resumeFile",                // must match @RequestParam name
	            "sample.pdf",
	            MediaType.APPLICATION_PDF_VALUE,
	            fileBytes
	        );
	        System.out.println("PDF byte length: " + fileBytes.length);


	        // 2. Perform the multipart apply request
	        mvc.perform(multipart("/app/apply/{jobId}", 1L)
	              .file(file)
	              .with(principal(seekerId.toString())))   // stub the Principal
	           .andExpect(status().isOk())
	           .andExpect(content().string("Application submitted successfully"));

	        // 3. Fetch the JobSeeker (to confirm they exist)
	        JobSeeker js = jobSeekerRepository.findByAccountId(seekerId)
	                                           .orElseThrow();

	        // 4. Fetch the Application for that seeker
	        List<Application> apps = applicationRepository.findByJobSeekerId(js.getId());
	        assertThat(apps).hasSize(1);

	        Application savedApp = apps.get(0);

	        // 5. Assert the resumeUrl was set
	        String savedFilename = savedApp.getResumeUrl();
	        assertThat(savedFilename)
	            .as("Application.resumeUrl should be set")
	            .isNotNull()
	            .startsWith("resume_" + seekerId)   // optional check on naming pattern
	            .endsWith(".pdf");

	        Path savedFile = UPLOAD_DIR.resolve(savedFilename);
	        System.out.println("Saved file path: " + savedFile.toAbsolutePath());
	        System.out.println("Saved file size: " + Files.size(savedFile)); // Should be 45778
	        assertThat(Files.exists(savedFile))
	            .as("Uploaded resume file should exist on disk")
	            .isTrue();
	    }


	    @Test
	    void downloadResume_streamsBackTheFile() throws Exception {
	        // simulate a previously uploaded resume
	        String filename = "resume_" + seekerId + "_test.pdf";
	        Path dest = UPLOAD_DIR.resolve(filename);
	        Files.write(dest, "PDF-DATA".getBytes());

	        // set that on the JobSeeker record
	        JobSeeker js = jobSeekerRepository.findByAccountId(seekerId).get();
	        js.setResumeUrl(filename);
	        jobSeekerRepository.save(js);

	        mvc.perform(get("/api/jobseeker/resume/{filename}", filename)
	                .with(principal(seekerId.toString())))   // <— and here
	           .andExpect(status().isOk())
	           .andExpect(header().string("Content-Type", MediaType.APPLICATION_PDF_VALUE))
	           .andExpect(header().string("Content-Disposition",
	                                      "attachment; filename=\"" + filename + "\""))
	           .andExpect(content().bytes(Files.readAllBytes(dest)));
	    }
	    
	    private RequestPostProcessor principal(String name) {
	    	  return request -> {
	    	    request.setUserPrincipal(() -> name);
	    	    return request;
	    	  };
	    	}
	}