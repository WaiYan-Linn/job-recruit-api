package cs.job.recruit.domain.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Data;

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
}
