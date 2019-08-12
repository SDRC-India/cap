package in.co.sdrc.cap.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.co.sdrc.cap.domain.Area;
import in.co.sdrc.cap.domain.Data;
import in.co.sdrc.cap.domain.Department;
import in.co.sdrc.cap.domain.Indicator;
import in.co.sdrc.cap.domain.Sector;
import in.co.sdrc.cap.domain.Source;

@Repository
public interface DataJpaRepository extends org.springframework.data.repository.Repository<Data, Long> {

	<S extends Data> List<S> save(Iterable<S> datas);

	public Data save(Data data);

	public List<Data> findAll();

	public List<Data> findByIndicatorAndAreaParentAreaCodeAndSrcAndTp(Indicator indicator, String code, Source src,
			String tp);

	Iterable<Data> findByIndicator(Indicator indicator);

	List<Data> findAllByLastModifiedGreaterThan(Date date);

	List<Data> findAllByLastModifiedGreaterThanOrderBySlugiddata(Date lastSynchronizedDate);
	
	@Query("SELECT MAX(dt.slugiddata) FROM Data dt ")
	long findMaxSlugId();

	List<Data> findAllByIndicatorKpiTrueOrIndicatorNitiaayogTrueOrIndicatorHmisTrueOrIndicatorSsvTrueOrIndicatorThematicKpiTrue();

	Iterable<Data> findByIndicatorAndTpIn(Indicator indicator, Set<String> timeperiod);

	List<Data> findByIndicatorInAndTpIn(List<Indicator> updatedIndicator, Set<String> values);

	List<Data> findByIndicatorIn(List<Indicator> updatedIndicator);

	List<Data> findByIndicatorInOrderBySlugiddataAsc(List<Indicator> updatedIndicator);

	List<Data> findByOrderBySlugiddataAsc();
	
	@Query("SELECT dt FROM Data dt")
	List<Data> findTopTen(Pageable pageable);
	
//	List<Data> findByIndicatorScIdAndAreaCode(int sectorId, String areaCode);
	
	List<Data> findAllBySectorIdAndAreaCodeAndIsLiveTrue(int sectorId, String areaCode);
	
	List<Data> findBySectorIdAndAreaCodeInAndIsLiveTrue(int sectorId, List<String> areaCodeList);
	
	List<Data> findByIndicatorInAndAreaCode(List<Indicator> indicators, String areaCode);
	
	List<Data> findAllByIndicatorIdAndSectorIdAndAreaIdAndIsBaselineTrueAndIsLiveTrue(int i, int s, int a);
	
	List<Data> findAllByIndicatorIdAndSectorIdAndAreaIdAndIsLiveTrue(int i, int s, int a);
	
	@Query(value="SELECT dt.id, dt.created_date, dkpirsrs, dnitirsrs, dthematicrsrs, ius, dt.last_modified, \r\n" + 
			"       periodicity, rank, slugiddata, tp, tps, trend, value, ar.code, \r\n" + 
			"       ind.id as indicatorId, src.id as sourceId, sub.id as subgroupId , cast (array_to_json(array_agg(topArea.code))as varchar) as top,cast (array_to_json(\r\n" + 
			"       array_agg(btmArea.code))as varchar) as bottom, is_baseline,target,sector_id_fk \r\n" + 
			"  FROM public.data dt inner join area ar  on dt.area_id_fk = ar.id inner join indicator ind on ind.id= dt.indicator_id_fk \r\n" + 
			"  inner join source src on src.id=dt.source_id_fk inner join subgroup sub on sub.id =dt.subgroup_id_fk \r\n" + 
			"  left join data_top_mapping dtp on dtp.data_id_fk = dt.id \r\n" + 
			" left join data_below_mapping dtb on dtb.data_id_fk = dt.id \r\n" + 
			" left join area topArea on topArea.id=dtp.area_id_fk\r\n" + 
			"  left join area btmArea on btmArea.id=dtb.area_id_fk\r\n" + 
			"  where dt.is_live = true \r\n" + 
			" group by \r\n" + 
			"dt.id, dt.created_date, dkpirsrs, dnitirsrs, dthematicrsrs, ius, dt.last_modified, \r\n" + 
			"       periodicity, rank, slugiddata, tp, tps, trend,is_baseline, target, value, ar.code, \r\n" + 
			"       ind.id, src.id, sub.id, is_baseline,target ",nativeQuery=true)
	List<Object []> findJsonData();
	
	List<Data> findAllByDepartmentIdAndAreaIdInAndIsLiveTrue(int id, List<Integer> ids);
	
	List<Data> findAllByIsLiveTrue();

	List<Data> findBySectorAndIndicatorAndSrcAndTpAndArea(Sector sector, Indicator indicator, Source src, String tp,
			Area area);

	List<Data> findBySectorAndIndicatorAndSrcAndTpAndAreaAndIsLiveTrue(Sector sector, Indicator indicator, Source src,
			String tp, Area area);

	@Query(value="select sc.id as sectorId, sc.sector_name, dp.id as departmentId, dp.department_name from sector sc "
			+ "Join sector_department_mapping sdm on sdm.sector_id_fk = sc.id JOIN department dp on sdm.department_id_fk = dp.id", nativeQuery=true)
	List<Object []> getDepartmentSector();

	List<Data> findAllBySectorIdAndAreaIdAndIsLiveTrue(int sectorId, int i);

	List<Data> findByAreaIdAndDepartmentIdAndSectorIdAndIsLiveTrueAndIsMRTTrue(Integer areaId, Integer departmentId,
			Integer sectorId);

	@Query(value="select da.indicator_id_fk, avg(cast(da.value as float )) as averag, " +
			"da.source_id_fk, da.tp,da.periodicity,da.time_period_id_fk from data da " + 
			" join "
			+ " (select daIn.indicator_id_fk as indicator,max(daIn.time_period_id_fk) as tpIn "
			+ " from data daIn group by daIn.indicator_id_fk,daIn.source_id_fk)  as mxm "
			+ " on da.indicator_id_fk = mxm.indicator and da.time_period_id_fk  = mxm.tpIn " +
			" where da.department_id_fk = :departmentId " + 
			"and da.sector_id_fk = :sectorId and da.is_live = true and da.is_mrt = true " + 
			"and da.area_id_fk in (select id from area where parent_area_code = :parentAreaCode) " + 
			"group by da.indicator_id_fk,da.source_id_fk, da.tp,da.periodicity,da.time_period_id_fk ", nativeQuery=true)
	List<Object[]> getAllBlockData(@Param("parentAreaCode")String parentAreaCode, 
								@Param("departmentId")Integer departmentId,
								@Param("sectorId")Integer sectorId);
	
	@Query(value="select da.indicator_id_fk, avg(cast(da.value as float )) as averag, " +
			"da.source_id_fk, da.tp,da.periodicity,da.time_period_id_fk from data da " + 
			" join "
			+ " (select daIn.indicator_id_fk as indicator,max(daIn.time_period_id_fk) as tpIn "
			+ " from data daIn group by daIn.indicator_id_fk,daIn.source_id_fk)  as mxm "
			+ " on da.indicator_id_fk = mxm.indicator and da.time_period_id_fk  = mxm.tpIn " +
			" where da.department_id_fk = :departmentId " + 
			"and da.is_live = true and da.is_mrt = true " + 
			"and da.area_id_fk in (select id from area where parent_area_code = :parentAreaCode) " + 
			"group by da.indicator_id_fk,da.source_id_fk, da.tp,da.periodicity,da.time_period_id_fk ", nativeQuery=true)
	List<Object[]> getAllBlockDataDept(@Param("parentAreaCode")String parentAreaCode, 
								@Param("departmentId")Integer departmentId);

	@Query(value="select da.indicator_id_fk, avg(cast(da.value as float )) as averag, " +
			"da.source_id_fk, da.tp,da.periodicity,da.time_period_id_fk from data da " + 
			" join "
			+ " (select daIn.indicator_id_fk as indicator,max(daIn.time_period_id_fk) as tpIn "
			+ " from data daIn group by daIn.indicator_id_fk,daIn.source_id_fk)  as mxm "
			+ " on da.indicator_id_fk = mxm.indicator and da.time_period_id_fk  = mxm.tpIn " +
			" where da.department_id_fk = :departmentId " + 
			"and da.sector_id_fk = :sectorId and da.is_live = true and da.is_mrt = true " + 
			"and da.area_id_fk in (select id from area where parent_area_code = :areaCode) " + 
			"group by da.indicator_id_fk,da.source_id_fk, da.tp,da.periodicity,da.time_period_id_fk ",nativeQuery=true)
	List<Object[]> getBlockData(@Param("areaCode")String areaCode, 
								@Param("departmentId")Integer departmentId, 
								@Param("sectorId")Integer sectorId);

	@Query(value="select distinct da.time_period_id_fk, da.tp from data da " + 
			"where da.indicator_id_fk = :indicatorId and da.is_live = true " +
			"and da.department_id_fk = :departmentId and da.sector_id_fk =:sectorId "+
			"and da.area_id_fk in (select id from area where parent_area_code = :parentAreaCode)", nativeQuery=true)
	List<Object[]> getTimePeriods(@Param("parentAreaCode")String parentAreaCode, 
									@Param("indicatorId")Integer indicatorId,
									@Param("departmentId")Integer departmentId, 
									@Param("sectorId")Integer sectorId);

	@Query(value="select distinct da.time_period_id_fk, da.tp from data da " + 
			"where da.indicator_id_fk = :indicatorId and da.is_live = true " +
			"and da.area_id_fk in (select id from area where parent_area_code = :parentAreaCode)", nativeQuery=true)
	List<Object[]> getTimePeriodsWTSectorDept(@Param("parentAreaCode")String parentAreaCode, @Param("indicatorId")Integer indicatorId);
	
	
	@Query(value="select distinct da.time_period_id_fk, da.tp from data da " + 
			"where da.indicator_id_fk = :indicatorId and da.is_live = true " +
			"and da.sector_id_fk =:sectorId "+
			"and da.area_id_fk in (select id from area where parent_area_code = :parentAreaCode)", nativeQuery=true)
	List<Object[]> getTimePeriodsSector(@Param("parentAreaCode")String parentAreaCode, 
									@Param("indicatorId")Integer indicatorId,
									@Param("sectorId")Integer sectorId);
	
	@Query(value="select distinct da.time_period_id_fk, da.tp from data da " + 
			"where da.indicator_id_fk = :indicatorId and da.is_live = true " +
			"and da.department_id_fk = :departmentId "+
			"and da.area_id_fk in (select id from area where parent_area_code = :parentAreaCode)", nativeQuery=true)
	List<Object[]> getTimePeriodsDept(@Param("parentAreaCode")String parentAreaCode, 
											@Param("indicatorId")Integer indicatorId,
											@Param("departmentId")Integer departmentId);
	

	@Query(value="select * from data da where da.time_period_id_fk= :timePeridId " + 
			"and da.indicator_id_fk = :indicatorId and da.is_live = true " + 
			"and da.department_id_fk = :departmentId and da.sector_id_fk =:sectorId "+
			"and da.area_id_fk in (select id from area where parent_area_code = :parentAreaCode)", nativeQuery=true)
	List<Data> getCardViewTableData(@Param("parentAreaCode")String parentAreaCode, 
												@Param("indicatorId")Integer indicatorId,
												@Param("timePeridId")Integer timePeridId,
												@Param("departmentId")Integer departmentId, 
												@Param("sectorId")Integer sectorId);
	
	@Query(value="select * from data da where da.time_period_id_fk= :timePeridId " + 
			"and da.indicator_id_fk = :indicatorId and da.is_live = true " + 
			"and da.department_id_fk = :departmentId "+
			"and da.area_id_fk in (select id from area where parent_area_code = :parentAreaCode)", nativeQuery=true)
	List<Data> getCardViewTableDataDept(@Param("parentAreaCode")String parentAreaCode, 
													@Param("indicatorId")Integer indicatorId,
													@Param("timePeridId")Integer timePeridId,
													@Param("departmentId")Integer departmentId);
	
	@Query(value="select * from data da where da.time_period_id_fk= :timePeridId " + 
			"and da.indicator_id_fk = :indicatorId and da.is_live = true " + 
			"and da.sector_id_fk =:sectorId "+
			"and da.area_id_fk in (select id from area where parent_area_code = :parentAreaCode)", nativeQuery=true)
	List<Data> getCardViewTableDataSector(@Param("parentAreaCode")String parentAreaCode, 
											@Param("indicatorId")Integer indicatorId,
											@Param("timePeridId")Integer timePeridId,
											@Param("sectorId")Integer sectorId);
	
	@Query(value="select * from data da where da.time_period_id_fk= :timePeridId " + 
			"and da.indicator_id_fk = :indicatorId and da.is_live = true " + 
			"and da.area_id_fk in (select id from area where parent_area_code = :parentAreaCode)", nativeQuery=true)
	List<Data> getCardViewTableDataWTSectorDept(@Param("parentAreaCode")String parentAreaCode, 
												@Param("indicatorId")Integer indicatorId,
												@Param("timePeridId")Integer timePeridId);
	
	@Modifying
	@Query(value="update data set is_live=false WHERE id=:id",nativeQuery = true)
	void updateDataIsLiveFalse(@Param("id") Long id);

	/*@Query(value="select r1.* from (select case when is_baseline = true and is_live = true then avg(cast(value as float )) end as base, " + 
			"case when is_mrt = true and is_live = true then avg(cast(value as float )) end as mrt, " + 
			"case when is_mrt = true and is_live = true then avg(cast(target as float )) end as tgt, " + 
			"indicator_id_fk,tp,time_period_id_fk from data  " + 
			"where sector_id_fk = :sectorId " + 
			"and department_id_fk = :deptId " + 
			"and is_live = true " + 
			"and area_id_fk in (select id from area where parent_area_code = :areaCode) "+
			"group by indicator_id_fk,is_baseline,is_mrt,tp,is_live,time_period_id_fk) as r1 " + 
			"where r1.base is not null or r1.mrt is not null order by r1.time_period_id_fk", nativeQuery=true)
	List<Object[]> getStateData(@Param("areaCode")String areaCode, @Param("sectorId")int sectorId, @Param("deptId")int deptId);
	
	@Query(value="select r1.* from (select case when is_baseline = true and is_live = true then avg(cast(value as float )) end as base, " + 
			"case when is_mrt = true and is_live = true then avg(cast(value as float )) end as mrt, " + 
			"case when is_mrt = true and is_live = true then avg(cast(target as float )) end as tgt, " + 
			"indicator_id_fk,tp,time_period_id_fk from data  " + 
			"where department_id_fk = :deptId " + 
			"and is_live = true " + 
			"and area_id_fk in (select id from area where parent_area_code = :areaCode) "+
			"group by indicator_id_fk,is_baseline,is_mrt,is_live,tp,time_period_id_fk) as r1 " + 
			"where r1.base is not null or r1.mrt is not null order by r1.time_period_id_fk", nativeQuery=true)
	List<Object[]> getStateDataUsingDept(@Param("areaCode")String areaCode, @Param("deptId")int deptId);
	
	@Query(value="select r1.* from (select case when is_baseline = true and is_live = true then avg(cast(value as float )) end as base, " + 
			"case when is_mrt = true and is_live = true then avg(cast(value as float )) end as mrt, " + 
			"case when is_mrt = true and is_live = true then avg(cast(target as float )) end as tgt, " + 
			"indicator_id_fk,tp,time_period_id_fk from data  " + 
			"where sector_id_fk = :sectorId " + 
			"and is_live = true " + 
			"and area_id_fk in (select id from area where parent_area_code = :areaCode) "+
			"group by indicator_id_fk,is_baseline,is_mrt,tp,is_live,time_period_id_fk) as r1 " + 
			"where r1.base is not null or r1.mrt is not null order by r1.time_period_id_fk", nativeQuery=true)
	List<Object[]> getStateDataUsingSector(@Param("areaCode")String areaCode, @Param("sectorId")int sectorId);
	*/

	@Query(value="select * from data da where da.time_period_id_fk= :timePeridId " + 
			"and da.indicator_id_fk = :indicatorId and da.is_live = true " + 
			"and department_id_fk = :departmentId and sector_id_fk =:sectorId "+
			"and da.area_id_fk in (select id from area where act_area_level_id_fk = :areaLevelBlock)", nativeQuery=true)
	List<Data> getCardViewTableBlockData(@Param("areaLevelBlock")Integer areaLevelBlock, 
										@Param("indicatorId")Integer indicatorId,
										@Param("timePeridId")Integer timePeridId,
										@Param("departmentId")Integer departmentId, 
										@Param("sectorId")Integer sectorId);

	@Query(value="select * from data da where da.indicator_id_fk = :indicatorId and da.is_live = true " + 
			"and da.area_id_fk in (select id from area where parent_area_code = :parentAreaCode)", nativeQuery=true)
	List<Data> getCardViewTableDataTP(@Param("parentAreaCode")String parentAreaCode, 
							@Param("indicatorId")Integer indicatorId);
	
	@Query(value="select * from data da where da.indicator_id_fk = :indicatorId and da.is_live = true " + 
			"and sector_id_fk =:sectorId "+
			"and da.area_id_fk in (select id from area where parent_area_code = :parentAreaCode)", nativeQuery=true)
	List<Data> getCardViewTableDataTPSector(@Param("parentAreaCode")String parentAreaCode, 
												@Param("indicatorId")Integer indicatorId, 
												@Param("sectorId")Integer sectorId);
	
	@Query(value="select * from data da where da.indicator_id_fk = :indicatorId and da.is_live = true " + 
			"and department_id_fk = :departmentId "+
			"and da.area_id_fk in (select id from area where parent_area_code = :parentAreaCode)", nativeQuery=true)
	List<Data> getCardViewTableDataTPDept(@Param("parentAreaCode")String parentAreaCode, 
											@Param("indicatorId")Integer indicatorId, 
											@Param("departmentId")Integer departmentId);
	
	@Query(value="select * from data da where da.indicator_id_fk = :indicatorId and da.is_live = true " + 
			"and department_id_fk = :departmentId and sector_id_fk =:sectorId "+
			"and da.area_id_fk in (select id from area where parent_area_code = :parentAreaCode)", nativeQuery=true)
	List<Data> getCardViewTableDataTPData(@Param("parentAreaCode")String parentAreaCode, 
											@Param("indicatorId")Integer indicatorId, 
											@Param("departmentId")Integer departmentId,	
											@Param("sectorId")Integer sectorId);

	List<Data> findAllByAreaIdAndIsLiveTrue(Integer id);

	List<Data> findAllBySectorIdAndAreaIdAndDepartmentIdAndIsLiveTrue(int sectorId, Integer id, Integer departmentId);

	@Query(value="select da.indicator_id_fk, avg(cast(da.value as float)) as averag, da.source_id_fk, tp, "+
			"periodicity,time_period_id_fk,department_id_fk,sector_id_fk from data da " + 
			" join "
			+ " (select daIn.indicator_id_fk as indicator,max(daIn.time_period_id_fk) as tpIn "
			+ " from data daIn group by daIn.indicator_id_fk,daIn.source_id_fk)  as mxm "
			+ " on da.indicator_id_fk = mxm.indicator and da.time_period_id_fk  = mxm.tpIn " +
			" where  da.is_live = true " + 
			"and da.is_mrt = true and da.indicator_id_fk in (:indicators) " + 
			"and da.area_id_fk in (select id from area " + 
			"where parent_area_code = :parentAreaCode) " + 
			"group by da.indicator_id_fk,source_id_fk, tp,periodicity,time_period_id_fk,department_id_fk,sector_id_fk",nativeQuery=true)
	List<Object[]> getData(@Param("parentAreaCode")String parentAreaCode, @Param("indicators")List<Integer> indicators);

	List<Data> findAllByAreaIdAndDepartmentIdAndIsLiveTrue(Integer id, Integer departmentId);

	List<Data> findAllByAreaParentAreaCodeAndIsMRTTrueAndIsLiveTrue(String string);

	List<Data> findBySectorAndIndicatorAndSrcAndTpAndAreaAndDepartmentAndIsLiveTrue(Sector sector, Indicator indicator,
			Source src, String tp, Area area, Department department);

	@Query(value="select da.indicator_id_fk, avg(cast(da.value as float )) as averag, " +
			"da.source_id_fk, da.tp,da.periodicity,da.time_period_id_fk from data da " + 
			" join "
			+ " (select daIn.indicator_id_fk as indicator,max(daIn.time_period_id_fk) as tpIn "
			+ " from data daIn group by daIn.indicator_id_fk,daIn.source_id_fk)  as mxm "
			+ " on da.indicator_id_fk = mxm.indicator and da.time_period_id_fk  = mxm.tpIn " +
			" where da.department_id_fk = :departmentId " + 
			"and da.sector_id_fk = :sectorId and da.is_live = true and da.is_mrt = true " + 
			"and da.area_id_fk in (select id from area where act_area_level_id_fk = :areaLevelBlock) " + 
			"group by da.indicator_id_fk,da.source_id_fk, da.tp,da.periodicity,da.time_period_id_fk ", nativeQuery=true)
	List<Object[]> getAllBlockDataForState(@Param("areaLevelBlock")Integer areaLevelBlock, 
											@Param("departmentId")Integer departmentId,
											@Param("sectorId")Integer sectorId);

	@Query(value="select da.indicator_id_fk, avg(cast(da.value as float )) as averag, " +
			"da.source_id_fk, da.tp,da.periodicity,da.time_period_id_fk from data da " + 
			" join "
			+ " (select daIn.indicator_id_fk as indicator,max(daIn.time_period_id_fk) as tpIn "
			+ " from data daIn group by daIn.indicator_id_fk,daIn.source_id_fk)  as mxm "
			+ " on da.indicator_id_fk = mxm.indicator and da.time_period_id_fk  = mxm.tpIn " +
			" where da.department_id_fk = :departmentId " + 
			"and da.is_live = true and da.is_mrt = true " + 
			"and da.area_id_fk in (select id from area where act_area_level_id_fk = :areaLevelBlock) " + 
			"group by da.indicator_id_fk,da.source_id_fk, da.tp,da.periodicity,da.time_period_id_fk ", nativeQuery=true)
	List<Object[]> getAllBlockDataDeptForState(@Param("areaLevelBlock")Integer areaLevelBlock, @Param("departmentId")Integer departmentId);

	@Query(value="select da.indicator_id_fk, avg(cast(da.value as float)) as averag, da.source_id_fk, tp, "+
			"periodicity,time_period_id_fk,department_id_fk,sector_id_fk from data da " + 
			" join "
			+ " (select daIn.indicator_id_fk as indicator,max(daIn.time_period_id_fk) as tpIn "
			+ " from data daIn group by daIn.indicator_id_fk,daIn.source_id_fk)  as mxm "
			+ " on da.indicator_id_fk = mxm.indicator and da.time_period_id_fk  = mxm.tpIn " +
			" where  da.is_live = true " + 
			"and da.is_mrt = true and da.indicator_id_fk in (:indicators) " + 
			"and da.area_id_fk in (select id from area " + 
			"where act_area_level_id_fk = :areaLevelBlock) " + 
			"group by da.indicator_id_fk,source_id_fk, tp,periodicity,time_period_id_fk,department_id_fk,sector_id_fk",nativeQuery=true)
	List<Object[]> getDataForState(@Param("areaLevelBlock")Integer areaLevelBlock, @Param("indicators")List<Integer> indicators);

	@Query(value="select * from data da where da.time_period_id_fk= :timePeridId " + 
			"and da.indicator_id_fk = :indicatorId and da.is_live = true " + 
			"and da.area_id_fk in (select id from area where act_area_level_id_fk = :areaLevelBlock)", nativeQuery=true)
	List<Data> getCardViewTableDataWTSectorDeptForState(@Param("areaLevelBlock")Integer areaLevelBlock, 
													@Param("indicatorId")Integer indicatorId,
													@Param("timePeridId")Integer timePeridId);
	
	@Query(value="select * from data da where da.time_period_id_fk= :timePeridId " + 
			"and da.indicator_id_fk = :indicatorId and da.is_live = true " + 
			"and da.sector_id_fk =:sectorId "+
			"and da.area_id_fk in (select id from area where act_area_level_id_fk = :areaLevelBlock)", nativeQuery=true)
	List<Data> getCardViewTableDataSectorForState(@Param("areaLevelBlock")Integer areaLevelBlock, @Param("indicatorId")Integer indicatorId,
			@Param("timePeridId")Integer timePeridId, @Param("sectorId")Integer sectorId);

	@Query(value="select * from data da where da.time_period_id_fk= :timePeridId " + 
			"and da.indicator_id_fk = :indicatorId and da.is_live = true " + 
			"and da.department_id_fk = :departmentId "+
			"and da.area_id_fk in (select id from area where act_area_level_id_fk = :areaLevelBlock)", nativeQuery=true)
	List<Data> getCardViewTableDataDeptForState(@Param("areaLevelBlock")Integer areaLevelBlock, @Param("indicatorId")Integer indicatorId, 
			@Param("timePeridId")Integer timePeridId,@Param("departmentId")Integer departmentId);

	@Query(value="select distinct da.time_period_id_fk, da.tp from data da " + 
			"where da.indicator_id_fk = :indicatorId and da.is_live = true " +
			"and da.area_id_fk in (select id from area where act_area_level_id_fk = :areaLevelBlock)", nativeQuery=true)
	List<Object[]> getTimePeriodsWTSectorDeptForState(@Param("areaLevelBlock")Integer areaLevelBlock, @Param("indicatorId")Integer indicatorId);

	@Query(value="select distinct da.time_period_id_fk, da.tp from data da " + 
			"where da.indicator_id_fk = :indicatorId and da.is_live = true " +
			"and da.sector_id_fk =:sectorId "+
			"and da.area_id_fk in (select id from area where act_area_level_id_fk = :areaLevelBlock)", nativeQuery=true)
	List<Object[]> getTimePeriodsSectorForState(@Param("areaLevelBlock")Integer areaLevelBlock, 
			@Param("indicatorId")Integer indicatorId, @Param("sectorId")Integer sectorId);

	@Query(value="select distinct da.time_period_id_fk, da.tp from data da " + 
			"where da.indicator_id_fk = :indicatorId and da.is_live = true " +
			"and da.department_id_fk = :departmentId "+
			"and da.area_id_fk in (select id from area where act_area_level_id_fk = :areaLevelBlock)", nativeQuery=true)
	List<Object[]> getTimePeriodsDeptForState(@Param("areaLevelBlock")Integer areaLevelBlock, @Param("indicatorId")Integer indicatorId, 
			@Param("departmentId")Integer departmentId);

	@Query(value="select distinct da.time_period_id_fk, da.tp from data da " + 
			"where da.indicator_id_fk = :indicatorId and da.is_live = true " +
			"and da.department_id_fk = :departmentId and da.sector_id_fk =:sectorId "+
			"and da.area_id_fk in (select id from area where act_area_level_id_fk = :areaLevelBlock)", nativeQuery=true)
	List<Object[]> getTimePeriodsForState(@Param("areaLevelBlock")Integer areaLevelBlock, 
											@Param("indicatorId")Integer indicatorId,
											@Param("departmentId")Integer departmentId, 
											@Param("sectorId")Integer sectorId);

	/*@Query(value="select da.indicator_id_fk, avg(cast(da.value as float )) as base, avg(cast(da.target as float )) as tgt," + 
			"da.source_id_fk, da.tp,da.periodicity from data da " + 
			"where da.department_id_fk = :departmentId " + 
			"and da.is_live = true and da.is_baseline = true " + 
			"and da.area_id_fk in (:areaIds) " + 
			"group by da.indicator_id_fk,da.source_id_fk, da.tp,da.periodicity", nativeQuery=true)
	List<Object[]> getStateDataUsingDeptBaseLine(@Param("areaIds")List<Integer> areaIds, @Param("departmentId")Integer departmentId);
	
	@Query(value="select da.indicator_id_fk, avg(cast(da.value as float )) as mrt, avg(cast(da.target as float )) as tgt, " + 
			"da.source_id_fk, da.tp,da.periodicity,da.area_id_fk from data da " + 
			"join " + 
			"(select daIn.indicator_id_fk as indicator,max(daIn.time_period_id_fk) as tpIn " + 
			"from data daIn group by daIn.indicator_id_fk,daIn.source_id_fk )  as mxm " + 
			"on da.indicator_id_fk = mxm.indicator and da.time_period_id_fk  = mxm.tpIn " + 
			"where da.department_id_fk = :departmentId " + 
			"and da.is_live = true and da.is_mrt = true " + 
			"and da.area_id_fk in (select id from area where parent_area_code = :areaCode)   " + 
			"group by da.indicator_id_fk, da.source_id_fk, da.tp, da.periodicity, da.area_id_fk",nativeQuery=true)
	List<Object[]> getStateDataUsingDeptMRTAndTGT(@Param("areaCode")String areaCode, @Param("departmentId")Integer departmentId);

	@Query(value="select da.indicator_id_fk, avg(cast(da.value as float )) as mrt, avg(cast(da.target as float )) as tgt, " + 
			"da.source_id_fk, da.tp,da.periodicity,da.area_id_fk from data da " + 
			"join " + 
			"(select daIn.indicator_id_fk as indicator,max(daIn.time_period_id_fk) as tpIn " + 
			"from data daIn group by daIn.indicator_id_fk,daIn.source_id_fk )  as mxm " + 
			"on da.indicator_id_fk = mxm.indicator and da.time_period_id_fk  = mxm.tpIn " + 
			"where da.sector_id_fk = :sectorId " + 
			"and da.is_live = true and da.is_mrt = true " + 
			"and da.area_id_fk in (select id from area where parent_area_code = :areaCode)   " + 
			"group by da.indicator_id_fk, da.source_id_fk, da.tp, da.periodicity, da.area_id_fk",nativeQuery=true)
	List<Object[]> getStateDataUsingSectorMRTAndTGT(@Param("areaCode")String areaCode, @Param("sectorId")Integer sectorId);

	@Query(value="select da.indicator_id_fk, avg(cast(da.value as float )) as base, avg(cast(da.target as float )) as tgt," + 
			"da.source_id_fk, da.tp,da.periodicity from data da " + 
			"where da.sector_id_fk = :sectorId " + 
			"and da.is_live = true and da.is_baseline = true " + 
			"and da.area_id_fk in (:areaIds) " + 
			"group by da.indicator_id_fk,da.source_id_fk, da.tp,da.periodicity", nativeQuery=true)
	List<Object[]> getStateDataUsingSectorBaseLine(@Param("areaIds")List<Integer> areaIds, @Param("sectorId")Integer sectorId);

	@Query(value="select da.indicator_id_fk, avg(cast(da.value as float )) as mrt, avg(cast(da.target as float )) as tgt, " + 
			"da.source_id_fk, da.tp,da.periodicity,da.area_id_fk from data da " + 
			"join " + 
			"(select daIn.indicator_id_fk as indicator,max(daIn.time_period_id_fk) as tpIn " + 
			"from data daIn group by daIn.indicator_id_fk,daIn.source_id_fk )  as mxm " + 
			"on da.indicator_id_fk = mxm.indicator and da.time_period_id_fk  = mxm.tpIn " + 
			"where da.sector_id_fk = :sectorId and da.department_id_fk = :departmentId " + 
			"and da.is_live = true and da.is_mrt = true " + 
			"and da.area_id_fk in (select id from area where parent_area_code = :areaCode)   " + 
			"group by da.indicator_id_fk, da.source_id_fk, da.tp, da.periodicity, da.area_id_fk",nativeQuery=true)
	List<Object[]> getStateDataMRTAndTGT(@Param("areaCode")String areaCode, 
										@Param("sectorId")Integer sectorId, 
										@Param("departmentId")Integer departmentId);

	@Query(value="select da.indicator_id_fk, avg(cast(da.value as float )) as base, avg(cast(da.target as float )) as tgt," + 
			"da.source_id_fk, da.tp,da.periodicity from data da " + 
			"where da.sector_id_fk = :sectorId and da.department_id_fk = :departmentId " + 
			"and da.is_live = true and da.is_baseline = true " + 
			"and da.area_id_fk in (:areaIds) " + 
			"group by da.indicator_id_fk,da.source_id_fk, da.tp,da.periodicity", nativeQuery=true)
	List<Object[]> getStateDataBaseLine(@Param("areaIds")List<Integer> areaIds, 
											@Param("sectorId")Integer sectorId, @Param("departmentId")Integer departmentId);

	*/
}
