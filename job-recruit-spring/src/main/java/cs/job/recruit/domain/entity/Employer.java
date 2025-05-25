package cs.job.recruit.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Employer {


    @Id
    private UUID id;
    
    @Column(nullable = false)
    private String companyName;
    
    @Column
    private String website;
    
    @Column
    private String profilePictureUrl;
    
    @JdbcTypeCode( SqlTypes.LONGVARCHAR )
    @Column(columnDefinition = "TEXT")
    private String aboutUs;

    
    @OneToOne
    @MapsId
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    // One company can have many jobs
    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL)
    private List<Job> jobs = new ArrayList<>();
}
