package in.co.sdrc.cap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.co.sdrc.cap.domain.Periodicity;

/**
 * @author Subham Ashish(subham@sdrc.co.in)
 *
 */
public interface PeriodicityRepository extends JpaRepository<Periodicity, Integer> {

	Periodicity findByPeriodicity(int i);

	List<Periodicity> findByIsLiveTrue();

}
