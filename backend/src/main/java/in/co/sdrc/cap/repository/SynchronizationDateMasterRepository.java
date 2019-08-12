package in.co.sdrc.cap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.co.sdrc.cap.domain.SynchronizationDateMaster;

@Repository
public interface SynchronizationDateMasterRepository extends  JpaRepository<SynchronizationDateMaster,Integer> {

	SynchronizationDateMaster findByTableName(String tableName);
	
}
