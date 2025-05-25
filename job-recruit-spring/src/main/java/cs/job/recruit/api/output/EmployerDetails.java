package cs.job.recruit.api.output;

import java.util.UUID;

public record EmployerDetails(
	    UUID id,
	    String companyName,
	    String website,
	    String profilePictureUrl,
	    String aboutUs,
	    String address,
	    String phoneNumber
	) {}
