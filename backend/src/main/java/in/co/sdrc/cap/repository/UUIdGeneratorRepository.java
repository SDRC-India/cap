package in.co.sdrc.cap.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.co.sdrc.cap.domain.UUIdGenerator;


/**
 * @author Subham Ashish(subham@sdrc.co.in)
 *
 */
public interface UUIdGeneratorRepository extends JpaRepository<UUIdGenerator, Integer>{

	UUIdGenerator findByMonthAndYearAndAccountId(int i, int j, int id);

	UUIdGenerator findByUuidAndMonthAndYear(String uuidValue, Integer month, Integer year);

	UUIdGenerator findByUuid(String uuidValue);

}
