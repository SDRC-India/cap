package in.co.sdrc.cap.repository;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import in.co.sdrc.cap.domain.Source;

@Repository

public interface SourceJpaRepository extends org.springframework.data.repository.Repository<Source,Long>{

	
	public Source save(Source source);

	public Source findBySourceName(String string);
	

	public List<Source> findAll();

	public List<Source> findAllByLastModifiedGreaterThan(Date date);

	public List<Source> findAllByLastModifiedGreaterThanOrderBySlugidsource(Date lastSynchronizedDate);
}