package in.co.sdrc.cap.service;

import java.util.List;

import in.co.sdrc.cap.dashboard.model.DashboardAreaModel;
import in.co.sdrc.cap.model.Department;
import in.co.sdrc.cap.model.UserDetailsModel;

public interface IUserManagementService {
	
	List<UserDetailsModel> getAllUsers();

	List<Department> getAllDepartments();

	List<DashboardAreaModel> districts();

}
