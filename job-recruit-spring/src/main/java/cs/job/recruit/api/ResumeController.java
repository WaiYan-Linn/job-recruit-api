package cs.job.recruit.api;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cs.job.recruit.domain.entity.JobSeeker;
import cs.job.recruit.domain.repository.JobSeekerRepository;
import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("/api/jobseeker")
public class ResumeController {
	
	
		@Autowired
		private JobSeekerRepository jobSeekerRepository;
		
	    private static  Path RESUME_DIR = Paths.get("uploads/resumes");

	    @GetMapping("/resume/{filename:.+}")
	    public ResponseEntity<Resource> downloadResume(
	            @PathVariable String filename,
	            Principal principal
	    ) throws IOException, MalformedURLException {
	        // 1. Authorization check (example: only owner can download)
	        UUID accountId = UUID.fromString(principal.getName());
	        JobSeeker seeker = jobSeekerRepository.findByAccountId(accountId)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
	       
	        // 2. Load file as resource
	        Path filePath = RESUME_DIR.resolve(filename).normalize();
	        if (!Files.exists(filePath)) {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	        }
	        Resource resource = new UrlResource(filePath.toUri());

	        // 3. Determine content type
	        String ext = StringUtils.getFilenameExtension(filename).toLowerCase();

	        MediaType mediaType;
	        switch (ext) {
	          case "pdf":  mediaType = MediaType.APPLICATION_PDF; break;
	          case "doc":  mediaType = MediaType.parseMediaType("application/msword"); break;
	          case "docx": mediaType = MediaType.parseMediaType(
	                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
	                      break;
	          default:     mediaType = MediaType.APPLICATION_OCTET_STREAM;
	        }

	        // 4. Build response with headers
	        return ResponseEntity.ok()
	                .contentType(mediaType)
	                .header(HttpHeaders.CONTENT_DISPOSITION,
	                        "attachment; filename=\"" + resource.getFilename() + "\"")
	                .body(resource);
	    }
	}


