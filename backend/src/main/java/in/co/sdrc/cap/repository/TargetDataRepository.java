package in.co.sdrc.cap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.co.sdrc.cap.domain.Area;
import in.co.sdrc.cap.domain.Indicator;
import in.co.sdrc.cap.domain.TargetData;
import in.co.sdrc.cap.domain.TimePeriod;

/**
 * @author subham
 *
 */
public interface TargetDataRepository extends JpaRepository<TargetData, Integer> {

	List<TargetData> findByIndicatorAndAreaAndTimePeriodAndIsLiveTrue(Indicator indicator, Area area,
			TimePeriod timePeriod);

	@Query(value="select td.* from target_data td where td.time_period_id_fk = :timePeridId " + 
			"and td.area_id_fk in (:areaIds) and td.indicator_id_fk in (:indicatorIds)",nativeQuery=true)
	List<TargetData> updateTargetData(@Param("indicatorIds")List<Integer> indicatorIds, 
			@Param("areaIds")List<Integer> areaIds, @Param("timePeridId")Integer timePeridId);

	List<TargetData> findAllByIsLiveTrue();

}
