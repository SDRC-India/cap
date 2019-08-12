package in.co.sdrc.cap.controller;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.co.sdrc.cap.service.ConfigurationService;

@RestController
public class ConfigurationController {

	@Autowired
	private ConfigurationService configurationService;
	
	@PostMapping("createOneAutherity")
	@PreAuthorize("hasAuthority('USER_MGMT_ALL_API')")
	public String createOneAutherity(@RequestParam("name") String name, @RequestParam("description") String description){
		return configurationService.createOneAutherity(name, description);
	}
	
	@PostMapping("createOneDesignation")
	@PreAuthorize("hasAuthority('USER_MGMT_ALL_API')")
	public String createOneDesignation(@RequestParam("name") String name, @RequestParam("code") String code){
		return configurationService.createOneDesignation(name, code);
	}
	
	@PostMapping("createOneDesignationAutherityMapping")
	@PreAuthorize("hasAuthority('USER_MGMT_ALL_API')")
	public String createOneDesignationAutherityMapping(@RequestParam("designationCode") String designationCode, @RequestParam("autherityName") String autherityName){
		return configurationService.createOneDesignationAutherityMapping(designationCode, autherityName);
	}
	
	@GetMapping("configureSQLDb")
	public boolean configureSQLDb(){
		return configurationService.configureSQLDb();
	}
	
	@GetMapping("configurePeriodicity")
	public boolean configurePeriodicity(){
		return configurationService.configurePeriodicity();
	}
	
	@GetMapping("configureTargetValue")
	public boolean configureTargetValue(){
		return configurationService.configureTargetValue();
	}
	
	@GetMapping("importDataIntoSQLDb")
	public boolean importDataIntoSQLDb(){
		return configurationService.importDataIntoSQLDb(new Date());
	}

}