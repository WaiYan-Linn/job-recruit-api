package cs.job.recruit.api.input;

import java.util.ArrayList;
import java.util.UUID;

import cs.job.recruit.domain.entity.Job;
import cs.job.recruit.domain.entity.Job.Category;
import cs.job.recruit.domain.entity.Job_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public record JobSearch(String category,String location,String keyword) {
	
	public Predicate[] where(CriteriaBuilder cb,Root<Job> root) {
		
		var params = new ArrayList<Predicate>();
		
		if(category != null && !category.isBlank()) {
			params.add(cb.equal(root.get(Job_.CATEGORY), Category.valueOf(category) ));
		}
		
		if(location != null && !location.isBlank()) {
			params.add(cb.equal(root.get(Job_.location), "%"+ location.toLowerCase()+"%"));
		}
		
		if(keyword !=null && !keyword.isBlank()) {
			String pattern = "%"+keyword.toLowerCase()+"%";
			Predicate titleLike = cb.like(root.get(Job_.title), pattern);
			Predicate descLike  = cb.like(root.get(Job_.description), pattern);
			Predicate requPredicate = cb.like(root.get(Job_.requirements), pattern);
			params.add(cb.or(titleLike,descLike,requPredicate));
		}
		
		return  params.toArray(new Predicate[0]);
	}

}
