package in.co.sdrc.cap.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

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

public interface IDashboardService {
	ResponseEntity<List<DashboardDataModel>> getFilteredData(String areaCode, int sectorId, Integer departmentId);

	ResponseEntity<List<DashboardAreaModel>> getArea();

	ResponseEntity<List<DashboardThemeModel>> getTheme();
	
	ResponseEntity<List<DepartmentModel>> getDepartment(String typeName);

	String pdfDownload(List<SVGModel> svgModels, String stateName, String districtName, String blockName, HttpServletRequest request);
	
	String excelDownload(SVGModelForExcel svgModel, HttpServletRequest request);

	List<Map<Object, List<NFHSDataModel>>> getNFHSData();

	Map<String, List<IndicatorGroupModel>> getCardViewData(String areaCode, Integer departmentId, Integer sectorId, Integer areaLevelId);

	List<ValueObject> getCardViewTimePeriods(String parentAreaCode, Integer indicatorId, Integer departmentId, Integer sectorId, Integer areaLevelId);

	List<CardViewTableModel> getCardViewTableData(String parentAreaCode, Integer indicatorId, 
			Integer timePeridId, Integer areaLevelId, Integer departmentId, Integer sectorId);

	Map<String, Object> getCardViewTableDataWithTP(String parentAreaCode, Integer indicatorId, Integer timePeridId,
			Integer areaLevelId, Integer departmentId, Integer sectorId);

	List<ValueObject> getFinancialYear();

	TargetModel getTargetData(String parentAreaCode, Integer sectorId, Integer timePeridId);

	String saveTargetData(TargetModel targetModel);
}
