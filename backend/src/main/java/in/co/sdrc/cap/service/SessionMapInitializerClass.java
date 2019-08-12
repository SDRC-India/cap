package in.co.sdrc.cap.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sdrc.usermgmt.core.util.IUserManagementHandler;
import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.domain.AccountDesignationMapping;
import org.sdrc.usermgmt.domain.Designation;
import org.sdrc.usermgmt.repository.AccountRepository;
import org.sdrc.usermgmt.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.co.sdrc.cap.domain.Area;
import in.co.sdrc.cap.domain.Department;
import in.co.sdrc.cap.domain.UserDetails;
import in.co.sdrc.cap.repository.AreaJpaRepository;
import in.co.sdrc.cap.repository.CustomAccountDesignationMappingRepository;
import in.co.sdrc.cap.repository.DepartmentRepository;
import in.co.sdrc.cap.repository.UserDetailsRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SessionMapInitializerClass implements IUserManagementHandler {

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private DesignationRepository designationRepository;

	@Autowired
	private UserDetailsRepository userDetailsRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private AreaJpaRepository areaJpaRepository;
	
	@Autowired
	@Qualifier("customAccountDesignationMappingRepository")
	private CustomAccountDesignationMappingRepository customAccountDesignationMappingRepository;
	
	@Autowired
	private ConfigurableEnvironment env;	
	
	@Override
	@Transactional
	public Map<String, Object> sessionMap(Object account) {
		Map<String, Object> sessionMap = new HashMap<>();
		UserDetails userDetails = userDetailsRepository.findByAccount((Account) account);
		sessionMap.put("name", userDetails.getName());
		if(userDetails.getName().equals("Admin") || userDetails.getArea() == null) {
			sessionMap.put("area", env.getProperty("bihar.area.code"));
		}else {
			sessionMap.put("area", userDetails.getArea().getCode());
		}
		return sessionMap;
	}

	@Override
	@Transactional
	public boolean saveAccountDetails(Map<String, Object> map, Object account) {

		/**
		 * Before inserting in database, checking all the necessary information are exists or not.
		 */
		if (map.get("name") == null || map.get("name").toString().isEmpty())
			throw new RuntimeException("key : name not found in map");
		
		try {
			
			
			
			/**
			 * save UserDetails
			 */
			UserDetails user = new UserDetails();
			user.setAccount((Account) account);
			user.setName(map.get("name").toString());
			
			
			//Set department
			if (!(map.get("departmentId") == null || map.get("departmentId").toString().isEmpty())) {
				Department department = departmentRepository.findOne((Integer)map.get("departmentId"));
				if(department != null) {
					user.setDepartment(department);
				}
				
			}
			
			//Setting area
			if (!(map.get("areaCode") == null || map.get("areaCode").toString().isEmpty())) {
				
				Area area = areaJpaRepository.findByCode((String)map.get("areaCode"));
				if(area != null) {
					user.setArea(area);
				}
				
			}
			userDetailsRepository.save(user);

			return true;
		} catch (Exception e) {
			log.error("Action: while creating user with payload {} " + map, e);
			throw new RuntimeException(e);
		}

	}

//	@SuppressWarnings("unchecked")
	@Override
	public boolean updateAccountDetails(Map<String, Object> map, Object account, Principal p) {
		try {
			
			Account acc = (Account) account;
			
			UserDetails user = userDetailsRepository.findByAccountId(((Account)account).getId());
			List<Designation> designations = designationRepository.findAll();

			if (map.get("email") != null && !map.get("email").toString().isEmpty()) {
				acc.setEmail((String)map.get("email"));
			}

			if (map.get("name") != null && !map.get("name").toString().isEmpty()) {
				user.setName(map.get("name").toString());
			}
			
			//Set department
			if (!(map.get("departmentId") == null || map.get("departmentId").toString().isEmpty())) {
				Department department = departmentRepository.findOne((Integer)map.get("departmentId"));
				if(department != null) {
					user.setDepartment(department);
				}
				
			}
			
			//Setting area
			if (!(map.get("areaCode") == null || map.get("areaCode").toString().isEmpty())) {
				
				Area area = areaJpaRepository.findByCode((String)map.get("areaCode"));//Area can be null
				user.setArea(area);
				
			}else {
				user.setArea(null);
			}
			
			
			List<Integer> designationIds = new ArrayList<>();
			
			
			if (map.get("designationIds") != null && !map.get("designationIds").toString().isEmpty()) {

				Integer id = (Integer) map.get("designationIds");
				designationIds.add(id);
				
			}
			

			List<AccountDesignationMapping> accountDesignationMappings = customAccountDesignationMappingRepository.findByAccountAndEnableTrue((Account)account);
			
			//check to be deleted
			for(int i = 0; i < accountDesignationMappings.size();i++) {
				
				boolean flag = false;
				
				for(int j = 0; j <  designationIds.size();j++) {
					if(accountDesignationMappings.get(i).getDesignation().getId() == designationIds.get(j).intValue()) {
						flag = true;
					}
				}
				
				if(!flag) {
					accountDesignationMappings.get(i).setEnable(false);
				}
				
			}
			
			//check to add
			for(int i = 0; i <  designationIds.size();i++) {
			
				boolean flag = false;
				for(int j = 0; j < accountDesignationMappings.size();j++) {
					if(designationIds.get(i).intValue() == accountDesignationMappings.get(j).getDesignation().getId()) {
						flag = true;
					}
				}
				if(!flag) {
					
					AccountDesignationMapping accountDesignationMapping = new AccountDesignationMapping();
					accountDesignationMapping.setAccount((Account)account);
					accountDesignationMapping.setDesignation(getDesignation(designations, designationIds.get(i).intValue()));
					accountDesignationMapping.setEnable(true);					
					accountDesignationMappings.add(accountDesignationMapping);
					
				}
				
			}



			customAccountDesignationMappingRepository.save(accountDesignationMappings);
			userDetailsRepository.save(user);
			accountRepository.save(acc);
			
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Designation getDesignation(List<Designation> designations, int id) {
		
		for(int i = 0; i < designations.size();i++) {
			if(designations.get(i).getId() == id) {
				return designations.get(i); 
			}
		}
		
		return null;
	}

	@Override
	public List<?> getAllAuthorities() {
		
		return null;
	}

}