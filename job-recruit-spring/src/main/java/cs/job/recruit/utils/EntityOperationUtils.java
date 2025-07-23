package cs.job.recruit.utils;

import java.util.Optional;

public class EntityOperationUtils {

	public static<T, ID> T safeCall(Optional<T> optional, String domain, ID id) {
		return optional.orElseThrow(() -> new ApiBusinessException("There is no %s with id %s.".formatted(domain, id)));
	}
}