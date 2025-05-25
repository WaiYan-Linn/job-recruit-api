package cs.job.recruit.api;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cs.job.recruit.api.input.UpdateEmployerRequest;
import cs.job.recruit.api.output.EmployerDetails;
import cs.job.recruit.api.output.PageResult;
import cs.job.recruit.service.EmployerService;

@RestController
@RequestMapping("employer")
public class EmployerController {

    @Autowired
    private EmployerService employerService;
    
    @GetMapping("/all")
    public PageResult<EmployerDetails> getEmployerDetails(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        var result= employerService.getEmployerDetailsByName(name, page, size);
        return result;
    }
    @GetMapping("/profile/about")
    public EmployerDetails getProfile(Authentication authentication) {
        String email = authentication.getName();
        return employerService.getCurrentEmployerProfile(email);

    }
    
    @GetMapping("{id}")
    public EmployerDetails getEmployerById(@PathVariable UUID id) {
    	return employerService.getCurrentEmployerProfile(id);
    }

    @PutMapping("/profile/update")
    public EmployerDetails updateProfile(
            Authentication authentication,
            @RequestBody UpdateEmployerRequest req
    ) {
        return employerService.updateCurrentEmployer(authentication.getName(), req);
    }

    @PostMapping("/profile/picture")
    public String uploadPicture(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {
        String email = authentication.getName();
        return employerService.uploadProfilePicture(email, file);
    }
    
   

}
