package cs.job.recruit.domain.entity;

import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    // Common attributes for all accounts
    @Column
    private String phone;
    
    @Column
    private String address;
    
    @Column(nullable = false)
    private boolean activated = false;
    
    // Optional one-to-one relationships (only one will be non-null)
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Employer employer;
    
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private JobSeeker jobSeeker;

    public enum Role {
        ADMIN, EMPLOYER, JOBSEEKER
    }
    
    public String getDisplayName() {
        if (role == Role.EMPLOYER && employer != null) {
            return employer.getCompanyName();
        } else if (role == Role.JOBSEEKER && jobSeeker != null) {
            return jobSeeker.getPersonalName();
        }
        return null; // or fallback value
    }

}
