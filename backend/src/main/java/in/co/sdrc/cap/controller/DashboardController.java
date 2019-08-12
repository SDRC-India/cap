package in.co.sdrc.cap.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import in.co.sdrc.cap.dashboard.model.DashboardAreaModel;
import in.co.sdrc.cap.dashboard.model.DashboardDataModel;
import in.co.sdrc.cap.dashboard.model.DashboardThemeModel;
import in.co.sdrc.cap.dashboard.model.DepartmentModel;
import in.co.sdrc.cap.dashboard.model.NFHSDataModel;
import in.co.sdrc.cap.dashboard.model.SVGModel;
import in.co.sdrc.cap.dashboard.model.SVGModelForExcel;
import in.co.sdrc.cap.mobile.model.IndicatorGroupModel;
import in.co.sdrc.cap.model.CardViewTableModel;
import in.co.sdrc.cap.model.TargetModel;
import in.co.sdrc.cap.model.ValueObject;
import in.co.sdrc.cap.service.IDashboardService;

@RestController
public class DashboardController {
	
	@Autowired
	private IDashboardService dashboardService;
	
	@PostMapping("getFilteredData")
	@PreAuthorize("hasAnyAuthority('DASHBOARD', 'USER_MGMT_ALL_API')")
	public ResponseEntity<List<DashboardDataModel>> getFilteredData(@RequestParam("areaCode") String areaCode, 
														@RequestParam("sectorId") int sectorId,@RequestParam("departmentId") int departmentId){
		return dashboardService.getFilteredData(areaCode, sectorId, departmentId);
	}
	
	
	@PostMapping("area")
	@PreAuthorize("hasAnyAuthority('DASHBOARD', 'USER_MGMT_ALL_API')")
	public ResponseEntity<List<DashboardAreaModel>> getArea(){
		return dashboardService.getArea();
	}
	
	
	@GetMapping("departments")
	@PreAuthorize("hasAnyAuthority('DASHBOARD', 'USER_MGMT_ALL_API')")
	public ResponseEntity<List<DepartmentModel>> getDepartment(@RequestParam("typeName") String typeName){
		return dashboardService.getDepartment(typeName);
	}
	
	@PostMapping("theme")
	@PreAuthorize("hasAnyAuthority('DASHBOARD', 'USER_MGMT_ALL_API')")
	public ResponseEntity<List<DashboardThemeModel>> getTheme(){
		return dashboardService.getTheme();
	}
	
	@PostMapping("pdfDownload")
	@PreAuthorize("hasAnyAuthority('DASHBOARD', 'USER_MGMT_ALL_API')")
	public ResponseEntity<InputStreamResource> pdfDownload(@RequestBody List<SVGModel> svgModels, @RequestParam("stateName") String stateName, @RequestParam("districtName") String districtName, @RequestParam("blockName") String blockName, HttpServletResponse response, HttpServletRequest request){
		String filePath = "";
		try {
			filePath = dashboardService.pdfDownload(svgModels, stateName, districtName, blockName, request);
			File file = new File(filePath);

			HttpHeaders respHeaders = new HttpHeaders();
			respHeaders.add("Content-Disposition", "attachment; filename=" + file.getName());
			InputStreamResource isr = new InputStreamResource(new FileInputStream(file));

			file.delete();
			return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	@PostMapping(value = "excelDownload")
	@PreAuthorize("hasAnyAuthority('DASHBOARD', 'USER_MGMT_ALL_API')")
	public ResponseEntity<InputStreamResource> excelDownload(@RequestBody SVGModelForExcel svgModel, HttpServletResponse response, HttpServletRequest request) {

		String filePath = "";
		try {
			filePath = dashboardService.excelDownload(svgModel, request);
			File file = new File(filePath);

			HttpHeaders respHeaders = new HttpHeaders();
			respHeaders.add("Content-Disposition", "attachment; filename=" + file.getName());
			InputStreamResource isr = new InputStreamResource(new FileInputStream(file));

			file.delete();
			return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	@GetMapping("getNFHSData")
	@ResponseBody
//	@PreAuthorize("hasAnyAuthority('DASHBOARD', 'USER_MGMT_ALL_API')")
	public List<Map<Object, List<NFHSDataModel>>> getNFHSData(){
		return dashboardService.getNFHSData();
	}
	
	@GetMapping("getCardViewData")
	@ResponseBody
	public Map<String, List<IndicatorGroupModel>> getCardViewData(@RequestParam("areaCode") String areaCode,
									@RequestParam("departmentId") Integer departmentId,
									@RequestParam("sectorId") Integer sectorId,
									@RequestParam("areaLevelId") Integer areaLevelId){
//		String areaCode="IND010";
//		Integer areaLevelId = 2;
//		Integer departmentId=4;
//		Integer sectorId=11;
		return dashboardService.getCardViewData(areaCode, departmentId, sectorId, areaLevelId);
	}
	
	
	@GetMapping("getCardViewTimePeriods")
	@ResponseBody
	public List<ValueObject> getCardViewTimePeriods(@RequestParam("parentAreaCode") String parentAreaCode,
									@RequestParam("indicatorId") Integer indicatorId,
									@RequestParam("departmentId") Integer departmentId,
									@RequestParam("sectorId") Integer sectorId,
									@RequestParam("areaLevelId") Integer areaLevelId){
		
		return dashboardService.getCardViewTimePeriods(parentAreaCode, indicatorId,departmentId, sectorId, areaLevelId);
	}
	
	@GetMapping("getCardViewTableData")
	@ResponseBody
	public List<CardViewTableModel> getCardViewTableData(@RequestParam("parentAreaCode") String parentAreaCode,
									@RequestParam("indicatorId") Integer indicatorId,
									@RequestParam("timePeridId") Integer timePeridId,
									@RequestParam("areaLevelId") Integer areaLevelId,
									@RequestParam("departmentId") Integer departmentId,
									@RequestParam("sectorId") Integer sectorId){
		return dashboardService.getCardViewTableData(parentAreaCode, indicatorId, timePeridId,areaLevelId,departmentId, sectorId);
	}
	
	@GetMapping("getCardViewTableDataWithTP")
	@ResponseBody
	public Map<String, Object> getCardViewTableDataWithTP(/*@RequestParam("parentAreaCode") String parentAreaCode,
									@RequestParam("indicatorId") Integer indicatorId,
									@RequestParam("timePeridId") Integer timePeridId,
									@RequestParam("areaLevelId") Integer areaLevelId,
									@RequestParam("departmentId") Integer departmentId,
									@RequestParam("sectorId") Integer sectorId*/){
		Integer indicatorId = 24;
		String parentAreaCode = "IND010004";
		Integer areaLevelId = 3;
		Integer timePeridId = 5;
		Integer departmentId=1;
		Integer sectorId=10;
		return dashboardService.getCardViewTableDataWithTP(parentAreaCode, indicatorId, timePeridId,areaLevelId,departmentId, sectorId);
	}
	
	@GetMapping("financialYear")
	@ResponseBody
	public List<ValueObject> getFinancialYear(){
		return dashboardService.getFinancialYear();
	}
	
	@GetMapping("getTargetData")
	@ResponseBody
	public TargetModel TargetModel (@RequestParam("parentAreaCode") String parentAreaCode,
			@RequestParam("sectorId") Integer sectorId, @RequestParam("timePeridId") Integer timePeridId){
//		String parentAreaCode = "IND010004";
//		Integer sectorId=10;
//		Integer timePeridId = 5; 
		return dashboardService.getTargetData(parentAreaCode, sectorId, timePeridId);
	}
	
	@PostMapping("saveTargetData")
	@ResponseBody
	public String saveTargetData(@RequestBody TargetModel targetModel){
//		String parentAreaCode = "IND010004";
//		Integer sectorId=10;
//		Integer timePeridId = 5;
//		dashboardService.getTargetData(parentAreaCode, sectorId, timePeridId);
		return dashboardService.saveTargetData(targetModel);
	}

}
