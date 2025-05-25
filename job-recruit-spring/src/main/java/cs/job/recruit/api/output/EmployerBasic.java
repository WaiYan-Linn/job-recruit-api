package cs.job.recruit.api.output;

import java.util.UUID;

public record EmployerBasic(
	    UUID id,
	    String companyName,
	    String website,
	    String profilePictureUrl) {

}
