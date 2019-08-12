package in.co.sdrc.cap.repository;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import in.co.sdrc.cap.domain.AreaLevel;

@Repository
public interface AreaLevelJpaRepository extends org.springframework.data.repository.Repository<AreaLevel,Long> {
	
	public AreaLevel save(AreaLevel areaLevel);
	
	public AreaLevel findByLevel(Integer id);
	
	public List<AreaLevel> findAll();

	public List<AreaLevel> findAllByLastModifiedGreaterThan(Date date);

	public List<AreaLevel> findAllByLastModifiedGreaterThanOrderBySlugidarealevel(Date lastSynchronizedDate);

}
