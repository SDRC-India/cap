package in.co.sdrc.cap.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.sdrc.cap.dashboard.model.DashboardAreaModel;
import in.co.sdrc.cap.model.Department;
import in.co.sdrc.cap.model.UserDetailsModel;
import in.co.sdrc.cap.service.IUserManagementService;

@RestController
public class CapUserManagementController {
	
	@Autowired
	private IUserManagementService userManagementService;

	@RequestMapping(value = "getUsers")
	@PreAuthorize("hasAuthority('USER_MGMT_ALL_API')")
	public List<UserDetailsModel> getAllUsers() {

		return userManagementService.getAllUsers();
	}
	
	@RequestMapping(value = "allDepartments")
	@PreAuthorize("hasAuthority('USER_MGMT_ALL_API')")
	public List<Department> allDepartments() {

		return userManagementService.getAllDepartments();
	}
	
	@RequestMapping(value = "districts")
	@PreAuthorize("hasAuthority('USER_MGMT_ALL_API')")
	public List<DashboardAreaModel> districts() {

		return userManagementService.districts();
	}
}
