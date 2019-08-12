package in.co.sdrc.cap.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.co.sdrc.cap.domain.Area;

@Repository
public interface AreaJpaRepository extends  org.springframework.data.repository.Repository<Area,Integer>{

	public Area save(Area area);
	
	Area findOne(int areaId);

	public List<Area> findAll();

	public Area findByCode(String areaCode);

	public List<Area> findAllByLastModifiedGreaterThanOrderBySlugidarea(Date lastSynchronizedDate);

	public List<Area> findAllByOrderByActAreaLevelIdAsc();

	@Query(value="select a1.id,a1.code, a1.areaname,a2.areaname as \"parent_area_name\",al.area_level_name from area a1 Left JOIN "
			+ "area a2 ON a1.parent_area_code = a2.code INNER JOIN "
			+ "arealevel al ON a1.act_area_level_id_fk =al.id", nativeQuery=true)
	public List<Object[]> getAllAreaNames();
	
	List<Area> findAllByParentAreaCodeOrderById(String parentAreaCode);

	public Area findByAreaname(String areaName);

}