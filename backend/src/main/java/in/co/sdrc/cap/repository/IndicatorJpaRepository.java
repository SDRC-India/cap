package in.co.sdrc.cap.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.co.sdrc.cap.domain.Indicator;

@Repository
public interface IndicatorJpaRepository extends org.springframework.data.repository.Repository<Indicator,Long>{

	public Indicator save(Indicator indicator);
	
	public List<Indicator> findAll();


	public Indicator findByIName(String indicatorName);


	public List<Indicator> findAllByLastModifiedGreaterThanOrderBySlugidindicator(Date lastSynchronizedDate);
	
	@Query(value="select i.id,i.i_name,u.unit_name,s.subgroup_name,i.highisgood "
			+ "from indicator i INNER JOIN unit u on i.unit_id_fk = u.id INNER JOIN "
			+ "subgroup s on i.subgroup_id_fk = s.id", nativeQuery=true)
	public List<Object[]> getAllIndicatorUnitSubgroupNameList();
	
	
	public List<Indicator> findAllByScIdIn(List<Integer> ids);

	public Indicator findById(Integer indicatorId);

	public List<Indicator> findByDepartmentIdAndPeriodicityId(int i, int j);

	public List<Indicator> findByDepartmentIdAndRecSectorId(Integer departmentId, Integer sectorId);

	public List<Indicator> findByRecSectorId(Integer sectorId);

	public List<Indicator> findByDepartmentId(Integer departmentId);

	@Query(value="select ind.id as indicatorid,ind.i_name,ar.areaname,ar.id as areaid from indicator ind,area ar " + 
			"where ind.rec_sector_id_fk= :sectorId " + 
			"and ar.parent_area_code =:parentAreaCode order by ar.areaname", nativeQuery=true)
	public List<Object[]> getIndicators(@Param("parentAreaCode")String parentAreaCode, @Param("sectorId")Integer sectorId);
}