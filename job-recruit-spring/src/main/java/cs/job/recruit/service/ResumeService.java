package cs.job.recruit.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cs.job.recruit.domain.repository.JobSeekerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResumeService {

	private final JobSeekerRepository jobSeekerRepository;
	private final String root = Paths.get("/uploads/resumes").toString();
	
	@Transactional
	public String storeResume(String accountId, MultipartFile resumeFile) throws IOException {
	    // 1. Validate non-empty
	    if (resumeFile.isEmpty()) {
	        throw new IllegalArgumentException("Cannot upload empty file");
	    }

	    // 2. Compute upload directory and ensure it exists
	    //    Use a relative path under your project root (or make it configurable)
	    Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "resumes");
	    Files.createDirectories(uploadDir);

	    // 3. Determine extension
	    String original = resumeFile.getOriginalFilename();
	    if (original == null || !original.contains(".")) {
	        throw new IllegalArgumentException("Invalid file name");
	    }
	    String ext = original.substring(original.lastIndexOf('.')).toLowerCase();
	    if (!List.of(".pdf", ".doc", ".docx").contains(ext)) {
	        throw new IllegalArgumentException("Unsupported resume format: " + ext);
	    }

	    // 4. Build a safe, unique filename
	    String filename = String.format("resume_%s_%d%s",
	        accountId,
	        System.currentTimeMillis(),
	        ext
	    );

	    // 5. Copy bytes from the MultipartFile to disk
	    Path target = uploadDir.resolve(filename);
	    try (InputStream in = resumeFile.getInputStream()) {
	        Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
	    }

	    return filename;
	}

}
