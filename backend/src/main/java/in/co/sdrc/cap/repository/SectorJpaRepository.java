package in.co.sdrc.cap.repository;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import in.co.sdrc.cap.domain.Sector;

@Repository
public interface SectorJpaRepository extends org.springframework.data.repository.Repository<Sector,Long>{

	
	public Sector save(Sector sector);

	public Sector findBySectorName(String string);
	
	public List<Sector> findAll();
	
	public List<Sector> findAllByDepartmentsId(int id);

	public List<Sector> findAllByLastModifiedGreaterThan(Date date);

	public List<Sector> findAllByLastModifiedGreaterThanOrderBySlugidsector(Date lastSynchronizedDate);

}