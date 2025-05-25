package cs.job.recruit.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
public class JobSeeker {

    @Id
    private UUID id;
    
    @Column(nullable = false)
    private String personalName;
    
    // Example fields specific to job seekers
    @Column
    private String resumeUrl;
    
    @Column(length = 1024)
    private String profileSummary;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL)
    private List<Application> applications = new ArrayList<>();

}
