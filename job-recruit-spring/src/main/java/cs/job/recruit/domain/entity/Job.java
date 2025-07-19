package cs.job.recruit.domain.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Enumerated(EnumType.STRING)
    private Category category; // e.g. IT, Healthcare, Finance, etc.
   
    private String location;
    
    @Enumerated(EnumType.STRING)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    private WorkMode workMode;
    
    @Enumerated(EnumType.STRING)
    private Experience experience;

    private Double salaryMin;
    private Double salaryMax;
    
    @JdbcTypeCode( SqlTypes.LONGVARCHAR )
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @JdbcTypeCode( SqlTypes.LONGVARCHAR )
    @Column(columnDefinition = "TEXT")
    private String requirements;
    
    @JdbcTypeCode( SqlTypes.LONGVARCHAR )
    @Column(columnDefinition = "TEXT")
    private String benefits;
    
    private LocalDate deadline;
    private String applicationEmail;


    private LocalDate postedAt;
    
    @ManyToOne
    @JoinColumn(name = "employer_id")
    private Employer employer;
    
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Application> applications = new ArrayList<>();

    private boolean closed;
    
    public enum JobType {
        FULL_TIME,
        PART_TIME,
        INTERNSHIP
    }

    public enum WorkMode {
        ON_SITE,
        HYBRID,
        REMOTE
    }
    
    public enum Category {
    	IT,
    	Engineering,
    	Banking,
    	Sales,
    	Marketing,
    	Design,
    	Others
    }
    
    public enum Experience{
    	Entry,
    	Mid,
    	Senior
    	
    }

}
