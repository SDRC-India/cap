package in.co.sdrc.cap.repository;

import java.util.List;

import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.domain.AccountDesignationMapping;
import org.sdrc.usermgmt.repository.AccountDesignationMappingRepository;
import org.springframework.stereotype.Component;

@Component("customAccountDesignationMappingRepository")
public interface CustomAccountDesignationMappingRepository extends AccountDesignationMappingRepository{

	List<AccountDesignationMapping> findByAccountAndEnableTrue(Account account);

}
