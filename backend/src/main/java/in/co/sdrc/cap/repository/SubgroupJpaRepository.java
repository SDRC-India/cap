package in.co.sdrc.cap.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.co.sdrc.cap.domain.Subgroup;

@Repository
public interface SubgroupJpaRepository extends JpaRepository<Subgroup, Long> {

	// Subgroup save(Subgroup subgroup);

	// Subgroup findBySubgroupName(String name);

	public List<Subgroup> findAll();

	Subgroup findBySubgroupName(String subgroupName);

	List<Subgroup> findAllByLastModifiedGreaterThanOrderBySlugidsubgroup(Date lastSynchronizedDate);

	public Subgroup findById(int i);

}
