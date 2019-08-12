package in.co.sdrc.cap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.sdrc.cap.service.TimePeriodService;

@RestController
public class HomeController {
	
	@Autowired
	private TimePeriodService timePeriodService;
	
	@GetMapping("createMonthly")
	public String createMonthly(){
		timePeriodService.createMonthlyTimePeriod();
		return "Success";
	}
	
	@GetMapping("createQuartly")
	public String createQuartly(){
		timePeriodService.createQuarterlyTimePeriod();
		return "Success";
	}
	
	@GetMapping("createYearly")
	public String createYearly(){
		timePeriodService.createYearlyTimePeriod();
		return "Success";
	}

}
