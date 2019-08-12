package in.co.sdrc.cap.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.co.sdrc.cap.domain.TimePeriod;
import in.co.sdrc.cap.util.Frequency;

public interface TimePeriodRepository extends JpaRepository<TimePeriod, Integer> {

	TimePeriod findByTimePeriodId(Integer timePeriodId);

	List<TimePeriod> findByPeriodicityId(Integer id);

	TimePeriod findByFinancialYearAndPeriodicityFrequency(String financialYear, Frequency yearly);

	List<TimePeriod> findByPeriodicityPeriodicity(int i);

	@Query(value="select tp.* from time_period tp, periodicity p " + 
					"where tp.periodicity_id_fk = p.id and p.periodicity = :yearlyPeriodicity " + 
				"and :date >=tp.start_date\\:\\:date and :date <=tp.end_date\\:\\:date", nativeQuery = true)
	List<TimePeriod> getTimeperiods(@Param("date")Date date, @Param("yearlyPeriodicity")int yearlyPeriodicity);

}
