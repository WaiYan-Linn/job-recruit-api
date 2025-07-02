package cs.job.recruit.domain.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
    
    @JdbcTypeCode( SqlTypes.LONGVARCHAR )
    @Column(columnDefinition = "TEXT")
    private String profileSummary;
    
    @Column(nullable = true) // optional — true is the default
    LocalDate dateOfBirth;
    
    @ElementCollection
    @CollectionTable(
        name = "job_seeker_skills",
        joinColumns = @JoinColumn(name = "job_seeker_id")
    )
    @Column(name = "skills")
    private List<String> skills;
    
    @Column
    private String profilePictureUrl;

    
    @OneToOne
    @MapsId
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL)
    private List<Application> applications = new ArrayList<>();

}
