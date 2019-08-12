package in.co.sdrc.cap.repository;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import in.co.sdrc.cap.domain.Unit;

@Repository
public interface UnitJpaRepository extends org.springframework.data.repository.Repository<Unit, Long> {

	public Unit save(Unit unit);

	public Unit findByUnitName(String unitC);

	public List<Unit> findAll();

	public List<Unit> findAllByLastModifiedGreaterThanOrderBySlugidunit(Date date);
}
