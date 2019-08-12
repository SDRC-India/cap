package in.co.sdrc.cap.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.domain.AccountDesignationMapping;
import org.sdrc.usermgmt.domain.Designation;
import org.sdrc.usermgmt.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;

import in.co.sdrc.cap.dashboard.model.DashboardAreaModel;
import in.co.sdrc.cap.domain.Area;
import in.co.sdrc.cap.domain.UserDetails;
import in.co.sdrc.cap.model.Department;
import in.co.sdrc.cap.model.UserDetailsModel;
import in.co.sdrc.cap.repository.AreaJpaRepository;
import in.co.sdrc.cap.repository.DepartmentRepository;
import in.co.sdrc.cap.repository.UserDetailsRepository;
import in.co.sdrc.cap.util.Constant;

@Service
public class UserManagementServiceImpl implements IUserManagementService {
	
	@Autowired
	private AccountRepository accountRepository;

	
	@Autowired
	private ConfigurableEnvironment configurableEnvironment;
	
	@Autowired
	private UserDetailsRepository userDetailsRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private AreaJpaRepository areaJpaRepository;
	
	@Autowired
	private ConfigurableEnvironment env;

	@Override
	public List<UserDetailsModel> getAllUsers() {
		List<Account> accList = accountRepository.findAll();
		List<UserDetails> userDetails = userDetailsRepository.findAll();
		Map<Integer, UserDetails> userDetailsMap = userDetails.stream().collect(Collectors.toMap(u->u.getAccount().getId(), u->u));
		
		List<UserDetailsModel> modelList = new ArrayList<>();
		accList.stream().forEach(account -> {
			
			//check for admin designation and skip admin designation
			boolean userIsAdmin = false;
			List<AccountDesignationMapping> accountDesignationMappings = account.getAccountDesignationMapping();
			
			Map<Integer, Designation> assignedDesignations = new HashMap<>();
			
			
			for(int i = 0; i < accountDesignationMappings.size();i++) {
				Designation designation = accountDesignationMappings.get(i).getDesignation();
				if(accountDesignationMappings.get(i).getEnable()) {
					assignedDesignations.put(designation.getId(), designation);
				}
				if(designation.getName().equals(configurableEnvironment.getProperty(Constant.ADMIN_DESIGNATION_NAME))) {
					userIsAdmin = true;
				}
			}
			
			
			
			


			//If user is not admin, we will send it to UI
			if(!userIsAdmin) {
				UserDetailsModel model = new UserDetailsModel();
				model.setUserId(account.getId());
				model.setUserName(account.getUserName());
				model.setName(userDetailsMap.get(account.getId()).getName());
				model.setEnable(account.isEnabled());
				model.setEmail(account.getEmail());
				
				
				List<Integer> roles = new ArrayList<>();
				List<String> roleNames = new ArrayList<>();
				
				for (Map.Entry<Integer, Designation> entry : assignedDesignations.entrySet()) {
					roles.add(entry.getKey().intValue());
					roleNames.add(entry.getValue().getName());
				}
				
				
				model.setRoleId(roles);
				model.setRoleName(roleNames);
				try {
					model.setAreaCode(userDetailsMap.get(account.getId()).getArea().getCode());
				}catch(NullPointerException e) {}
				model.setDepartmentId(userDetailsMap.get(account.getId()).getDepartment().getId());
				modelList.add(model);
			}
			
			
			
		});
		return modelList;
			
	}
	

	@Override
	public List<Department> getAllDepartments() {
		List<in.co.sdrc.cap.domain.Department> departments = departmentRepository.findAll();
		List<Department> departmentList = new ArrayList<>();
		departments.stream().forEach(d->{
			Department dept = new Department();
			dept.setId(d.getId());
			dept.setName(d.getDepartmentName());
			departmentList.add(dept);
		});
		return departmentList;
	}

	@Override
	public List<DashboardAreaModel> districts() {
		List<Area> districts = areaJpaRepository.findAllByParentAreaCodeOrderById(env.getProperty("bihar.area.code"));
		List<DashboardAreaModel> models = new ArrayList<>();
		districts.stream().forEach(d->{
			DashboardAreaModel model = new DashboardAreaModel();
			model.setAreaname(d.getAreaname());
			model.setCode(d.getCode());
			model.setParentAreaCode(d.getParentAreaCode());
			models.add(model);
		});
		return models;
	}
			

}
