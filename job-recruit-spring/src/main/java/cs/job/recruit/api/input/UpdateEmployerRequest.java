package cs.job.recruit.api.input;

public record UpdateEmployerRequest(
	     String companyName,
	     String website,
	     String aboutUs,
	     String address,
	     String phoneNumber
	) {

}
