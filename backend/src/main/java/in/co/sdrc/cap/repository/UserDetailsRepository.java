package in.co.sdrc.cap.repository;

import org.sdrc.usermgmt.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import in.co.sdrc.cap.domain.UserDetails;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Integer> {

	UserDetails findByAccount(Account account);
	
	UserDetails findByAccountId(Integer id);

}
