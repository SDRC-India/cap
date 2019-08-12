package in.co.sdrc.cap.service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationConstraint.OperatorType;
import org.apache.poi.ss.usermodel.DataValidationConstraint.ValidationType;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import in.co.sdrc.cap.dataentry.model.AreaDE;
import in.co.sdrc.cap.dataentry.model.DepartmentDE;
import in.co.sdrc.cap.dataentry.model.DepartmentSectorIndicatorMapping;
import in.co.sdrc.cap.domain.Area;
import in.co.sdrc.cap.domain.Data;
import in.co.sdrc.cap.domain.Department;
import in.co.sdrc.cap.domain.Indicator;
import in.co.sdrc.cap.domain.Periodicity;
import in.co.sdrc.cap.domain.Sector;
import in.co.sdrc.cap.domain.Source;
import in.co.sdrc.cap.domain.Subgroup;
import in.co.sdrc.cap.domain.TargetData;
import in.co.sdrc.cap.domain.TimePeriod;
import in.co.sdrc.cap.domain.UUIdGenerator;
import in.co.sdrc.cap.domain.UserDetails;
import in.co.sdrc.cap.exceptions.IncorrectDataFormatException;
import in.co.sdrc.cap.exceptions.InvalidFileException;
import in.co.sdrc.cap.exceptions.SubmissionException;
import in.co.sdrc.cap.model.DataEntryTableModel;
import in.co.sdrc.cap.model.FrequencyModel;
import in.co.sdrc.cap.model.TargetEventModel;
import in.co.sdrc.cap.model.UserModel;
import in.co.sdrc.cap.repository.AreaJpaRepository;
import in.co.sdrc.cap.repository.DataJpaRepository;
import in.co.sdrc.cap.repository.DepartmentRepository;
import in.co.sdrc.cap.repository.IndicatorJpaRepository;
import in.co.sdrc.cap.repository.PeriodicityRepository;
import in.co.sdrc.cap.repository.SectorJpaRepository;
import in.co.sdrc.cap.repository.SourceJpaRepository;
import in.co.sdrc.cap.repository.SubgroupJpaRepository;
import in.co.sdrc.cap.repository.TargetDataRepository;
import in.co.sdrc.cap.repository.TimePeriodRepository;
import in.co.sdrc.cap.repository.UUIdGeneratorRepository;
import in.co.sdrc.cap.repository.UserDetailsRepository;
import in.co.sdrc.cap.util.CAPUtil;
import in.co.sdrc.cap.util.Constant;
import in.co.sdrc.cap.util.Frequency;
import in.co.sdrc.cap.util.TokenInfoExtracter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DataEntryServiceImpl implements IDataEntryService {

	@Autowired
	private AreaJpaRepository areaJpaRepository;

	@Autowired
	private SourceJpaRepository sourceJpaRepository;

	@Autowired
	private SectorJpaRepository sectorJpaRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private IndicatorJpaRepository indicatorJpaRepository;

	@Autowired
	private UserDetailsRepository userDetailsRepository;

	@Autowired
	private DataJpaRepository dataJpaRepository;

	@Autowired
	private TokenInfoExtracter tokenInfoExtracter;

	@Autowired
	private ConfigurableEnvironment env;

	@Autowired
	private UUIdGeneratorRepository uuidGeneratorRepository;

	@Autowired
	private SubgroupJpaRepository subgroupJpaRepository;

	@Autowired
	private MobileService mobileService;

	@Autowired
	private TargetDataRepository targetDataRepository;

	@Autowired
	private TimePeriodRepository timePeriodRepository;

	@Autowired
	private PeriodicityRepository periodicityRepository;

	@Qualifier("jpaAccountRepository")
	@Autowired
	private AccountRepository accountRepository;

	private final Path outputPath = Paths.get("/cap");

	@Override
	@Transactional
	public String generateTemplate() {

		// TODO Auto-generated method stub
		String outputPathExcel = outputPath.toAbsolutePath().toString();

		// Getting blocks

		OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext()
				.getAuthentication();

		UserModel userModel = tokenInfoExtracter.getUserModelInfo(authentication);

		UserDetails userDetails = userDetailsRepository.findOne(userModel.getId());
		Area district = userDetails.getArea();

		List<Area> areas = areaJpaRepository.findAllByParentAreaCodeOrderById(district.getCode());

		// get sources
		List<String> sourceList = new ArrayList<>();

		sourceJpaRepository.findAll().stream().forEach(s -> sourceList.add(s.getSourceName()));

		Collections.sort(sourceList);

		List<Sector> sectorList = sectorJpaRepository.findAllByDepartmentsId(userDetails.getDepartment().getId());
		List<Integer> sectorIds = new ArrayList<>();
		sectorList.stream().forEach(sector -> sectorIds.add(sector.getId()));

		List<Indicator> indicators = indicatorJpaRepository.findAllByScIdIn(sectorIds);

		List<DepartmentDE> ddList = new ArrayList<>();
		List<DepartmentSectorIndicatorMapping> mapping = new ArrayList<>();
		for (Indicator indicator : indicators) {
			DepartmentDE dd = new DepartmentDE();
			Map<Department, List<DepartmentSectorIndicatorMapping>> data = getDepartment(indicator, mapping);
			for (Map.Entry<Department, List<DepartmentSectorIndicatorMapping>> entry : data.entrySet()) {
				dd.setDepartment(entry.getKey());
				mapping = entry.getValue();
			}

			Map<Sector, List<DepartmentSectorIndicatorMapping>> dataSector = getSector(indicator,
					dd.getDepartment().getId(), mapping);
			if (dataSector == null) {
				dataSector = getSectorFromMapping(mapping, indicator, dd.getDepartment().getId());
				for (Map.Entry<Sector, List<DepartmentSectorIndicatorMapping>> entry : dataSector.entrySet()) {
					dd.setSector(entry.getKey());
					mapping = entry.getValue();
				}
			} else {
				for (Map.Entry<Sector, List<DepartmentSectorIndicatorMapping>> entry : dataSector.entrySet()) {
					dd.setSector(entry.getKey());
					mapping = entry.getValue();
				}
			}
			Map<Double, List<AreaDE>> areaMap = new HashMap<>();
			areaMap = getAreaDEList(areas, indicator, dd.getSector());

			for (Map.Entry<Double, List<AreaDE>> entry : areaMap.entrySet()) {
				dd.setAvg(entry.getKey());
				dd.setAreaList(entry.getValue());
			}

			dd.setIndicator(indicator);
			ddList.add(dd);
		}

		try {
			Date currentDate = new Date();

			String date = new SimpleDateFormat("yyyyMMddHHmmssS").format(new Date());
			String path = outputPathExcel;
			outputPathExcel = path + "_" + date + ".xlsx";

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("cap");
			sheet.protectSheet(env.getProperty(Constant.SHEET_PROTECTION_PASSWORD));
//			sheet.createFreezePane(0, 5);

			XSSFRow row = null;
			XSSFCell cell = null;
			int rowNum = 4;
			int cellNum = 0;
			int rowHeaderMergeVaue = 0;
			XSSFCellStyle cellstyleFont = CAPUtil.getHeaderStyle(workbook);
			XSSFCellStyle enableEdit = CAPUtil.getEnableEditCell(workbook);
			XSSFCellStyle indicatorStyle = CAPUtil.getIndicatorCell(workbook);
			XSSFCellStyle themeDepartment = CAPUtil.getThemeDepartmentCell(workbook);

			// 1st row, the header
			row = sheet.createRow(rowNum);

			cell = row.createCell(cellNum);
			cell.setCellValue("SL.NO");
			cell.setCellStyle(cellstyleFont);
			sheet.autoSizeColumn(cellNum);
			rowHeaderMergeVaue++;

			cell = row.createCell(++cellNum);
			cell.setCellValue("Theme");
			cell.setCellStyle(cellstyleFont);
			sheet.autoSizeColumn(cellNum);
			sheet.setColumnWidth(cellNum, 5000);
			rowHeaderMergeVaue++;

			cell = row.createCell(++cellNum);
			cell.setCellValue("Indicator with target");
			cell.setCellStyle(cellstyleFont);
			sheet.autoSizeColumn(cellNum);
			sheet.setColumnWidth(cellNum, 10000);
			rowHeaderMergeVaue++;

			cell = row.createCell(++cellNum);
			cell.setCellValue("Source");
			cell.setCellStyle(cellstyleFont);
			sheet.autoSizeColumn(cellNum);
			rowHeaderMergeVaue++;

			cell = row.createCell(++cellNum);
			cell.setCellValue(
					"Time period (Monthly:MMM-YYYY, Quartely:MMM-YYYY:MMM-YYYY, Yearly:YYYY, Financial year:YYYY-YYYY)");
			cell.setCellStyle(cellstyleFont);
			sheet.setColumnWidth(cellNum, 10000);
			rowHeaderMergeVaue++;

			// blocks
			for (int i = 0; i < areas.size(); i++) {
				cell = row.createCell(++cellNum);
				cell.setCellValue(areas.get(i).getAreaname());
				cell.setCellStyle(cellstyleFont);
				sheet.autoSizeColumn(cellNum);
				rowHeaderMergeVaue++;
			}

			// Average
			cell = row.createCell(++cellNum);
			cell.setCellValue("Average");
			cell.setCellStyle(cellstyleFont);
			sheet.autoSizeColumn(cellNum);
			rowHeaderMergeVaue++;

			// initial row headings
			sheet = workbook.getSheet("cap");
			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("CAP : Data Entry Template");
			cell.setCellStyle(cellstyleFont);
			sheet = CAPUtil.doMerge(0, 0, 0, rowHeaderMergeVaue - 1, sheet);

			row = sheet.createRow(1);
			cell = row.createCell(0);
			cell.setCellStyle(cellstyleFont);
			cell.setCellValue("STATE : Bihar, DISTRICT : " + district.getAreaname());
			sheet = CAPUtil.doMerge(1, 1, 0, rowHeaderMergeVaue - 1, sheet);

			row = sheet.createRow(2);
			cell = row.createCell(0);
			cell.setCellStyle(cellstyleFont);
			cell.setCellValue("DEPARTMENT :" + userDetails.getDepartment().getDepartmentName());
			sheet = CAPUtil.doMerge(2, 2, 0, rowHeaderMergeVaue - 1, sheet);

			row = sheet.createRow(3);
			cell = row.createCell(0);
			cell.setCellStyle(cellstyleFont);
			cell.setCellValue("Date Of Template Generation : " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
			sheet = CAPUtil.doMerge(3, 3, 0, rowHeaderMergeVaue - 1, sheet);

			row = sheet.createRow(rowNum + 1);

			int i = 0;
			DataValidation dataValidation = null;
			DataValidationConstraint constraint = null;
			DataValidationHelper validationHelper = null;

			int avgRowNum = 6;
			for (DepartmentDE dd : ddList) {

				cellNum = 0;
				row = sheet.createRow(++rowNum);
				row.setHeight((short) 2000);

				cell = row.createCell(cellNum++);
				cell.setCellStyle(themeDepartment);
				cell.setCellValue(++i);

				cell = row.createCell(cellNum++);
				cell.setCellStyle(themeDepartment);
				cell.setCellValue(dd.getSector().getSectorName());

				cell = row.createCell(cellNum++);
				cell.setCellStyle(indicatorStyle);
				cell.setCellValue(dd.getIndicator().getiName());

				int indicatorCellNum = cellNum;

				validationHelper = new XSSFDataValidationHelper(sheet);
				CellRangeAddressList addressList = new CellRangeAddressList(rowNum, rowNum, cellNum, cellNum);
				cell = row.createCell(cellNum++);
				sheet.setColumnWidth(cell.getColumnIndex(), 4000);
				sheet.setColumnWidth(cell.getColumnIndex(), 4000);
				cell.setCellStyle(enableEdit);
				sheet.setColumnWidth(cell.getColumnIndex(), 4000);
				constraint = validationHelper
						.createExplicitListConstraint(Arrays.stream(sourceList.toArray()).toArray(String[]::new));
				dataValidation = validationHelper.createValidation(constraint, addressList);
				dataValidation.setSuppressDropDownArrow(true);
				dataValidation.setShowErrorBox(true);
				dataValidation.setShowPromptBox(true);
				sheet.addValidationData(dataValidation);

//				cellNum++;

				// timeperiod
				cell = row.createCell(cellNum++);
				cell.setCellStyle(CAPUtil.getEnableEditCellForTimePeriod(workbook));

				for (AreaDE area : dd.getAreaList()) {
					cell = row.createCell(cellNum++);
					cell.setCellValue("");
					cell.setCellStyle(enableEdit);
				}

				String columnLetter = CellReference.convertNumToColString(4 + dd.getAreaList().size());

				cell = row.createCell(cellNum++);
				cell.setCellType(CellType.FORMULA);
				String cellString = "AVERAGE(F" + avgRowNum + ":" + columnLetter + "" + avgRowNum + ")";
				cell.setCellFormula(cellString);
				cell.setCellStyle(themeDepartment);

				avgRowNum += 2;

				row = sheet.createRow(++rowNum);
				cellNum = indicatorCellNum;
				cell = row.createCell(cellNum++);
				cell.setCellValue("1. Target 2019-20");
				sheet.autoSizeColumn(indicatorCellNum);
				sheet.setColumnWidth(indicatorCellNum, 6000);
				cell.setCellStyle(themeDepartment);

				cell = row.createCell(cellNum++);
				cell.setCellValue("2019-2020");
				cell.setCellStyle(themeDepartment);

				for (AreaDE area : dd.getAreaList()) {
					cell = row.createCell(cellNum++);
					cell.setCellValue(area.getTarget());
					cell.setCellStyle(themeDepartment);
				}

				cell = row.createCell(cellNum++);
				cell.setCellValue(dd.getAvg());
				cell.setCellStyle(themeDepartment);

				sheet = CAPUtil.doMerge(rowNum - 1, rowNum, 0, 0, sheet);

				sheet = CAPUtil.doMerge(rowNum - 1, rowNum, 1, 1, sheet);

				sheet = CAPUtil.doMerge(rowNum - 1, rowNum, 2, 2, sheet);

			}

			/**
			 * restrict sheet cell value to accept value between 0 to 100
			 * 
			 */

			CellRangeAddressList addressList = new CellRangeAddressList(4, 1000, 5, 100);

			validationHelper = sheet.getDataValidationHelper();
			constraint = validationHelper.createNumericConstraint(ValidationType.INTEGER, OperatorType.BETWEEN, "0",
					"100");
			dataValidation = validationHelper.createValidation(constraint, addressList);
			dataValidation.setShowErrorBox(true);
			sheet.addValidationData(dataValidation);

			/**
			 * uuid generator
			 */
			String uuidValue = null;
			UUIdGenerator uuidGenerator = new UUIdGenerator();
			/**
			 * check for same month year and userid whether uuidvalue exist or not
			 */
			UUIdGenerator uuidGen = uuidGeneratorRepository.findByMonthAndYearAndAccountId(currentDate.getMonth() + 1,
					currentDate.getYear() + 1900, userModel.getId());

			if (uuidGen == null) {
				UUID uuid = UUID.randomUUID();
				uuidValue = uuid.toString();
				uuidGenerator = new UUIdGenerator();
				uuidGenerator.setAccountId(userModel.getId());
				uuidGenerator.setCreatedDate(new Date());
				uuidGenerator.setMonth(currentDate.getMonth() + 1);
				uuidGenerator.setYear(currentDate.getYear() + 1900);
				uuidGenerator.setUuid(uuidValue);
			} else {
				uuidValue = uuidGen.getUuid();
			}

			// save uuid in db
			if (uuidGenerator.getUuid() != null)
				uuidGeneratorRepository.save(uuidGenerator);

			/**
			 * create one hidden and protected sheet and write uuid value
			 */

			sheet = workbook.createSheet("UUID_Verify");
			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("uuid");
			cell = row.createCell(1);
			cell.setCellValue(uuidValue);

			sheet = workbook.getSheet("UUID_Verify");
			sheet.protectSheet(Constant.SHEET_PROTECTION_PASSWORD);
			workbook.setSheetHidden(workbook.getSheetIndex("UUID_Verify"), XSSFWorkbook.SHEET_STATE_VERY_HIDDEN);

			XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
			log.info("After formula evaluation");
			FileOutputStream fileOutputStream = new FileOutputStream(new File(outputPathExcel));
			workbook.write(fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
			workbook.close();
			log.info("After workbook close");

		} catch (Exception e) {
			log.info("Exception occured" + e.getMessage());
			e.printStackTrace();
		}

		log.info("Before returning the excel path from service");
		return outputPathExcel;
	}

	private Map<Double, List<AreaDE>> getAreaDEList(List<Area> areas, Indicator indicator, Sector sector) {
		Map<Double, List<AreaDE>> returnMap = new HashMap<>();
		List<AreaDE> areaDEList = new ArrayList<>();
		int numberOfArea = 0;
		double totalTarget = 0;
		for (Area a : areas) {
			List<Data> data = dataJpaRepository.findAllByIndicatorIdAndSectorIdAndAreaIdAndIsBaselineTrueAndIsLiveTrue(
					indicator.getId(), sector.getId(), a.getId());
			AreaDE ad = new AreaDE();
			ad.setArea(a);
			if (data.size() > 0) {
				if (!data.get(0).getTarget().isEmpty()) {
					ad.setTarget(
							data.get(0).getTarget().isEmpty() ? null : Double.parseDouble(data.get(0).getTarget()));
					numberOfArea++;
					totalTarget += Double.parseDouble(data.get(0).getTarget());
				}

			} else {
				ad.setTarget(0);
			}

			areaDEList.add(ad);
		}

		double avg = Math.round(totalTarget / numberOfArea);
		returnMap.put(avg, areaDEList);
		return returnMap;
	}

	private Map<Sector, List<DepartmentSectorIndicatorMapping>> getSectorFromMapping(
			List<DepartmentSectorIndicatorMapping> mapping, Indicator indicator, Integer id) {
		Map<Sector, List<DepartmentSectorIndicatorMapping>> data = new HashMap<>();
		List<Sector> sectors = indicator.getSc();
		for (Sector sector : sectors) {

			for (DepartmentSectorIndicatorMapping m : mapping) {
				if (m.getSector() == sector.getId() && m.getIndicator() == indicator.getId()
						&& m.getDepartment() == id) {
					data.put(sector, mapping);
					return data;
				}
			}

			DepartmentSectorIndicatorMapping m = new DepartmentSectorIndicatorMapping();
			m.setDepartment(id);
			m.setSector(sector.getId());
			m.setIndicator(indicator.getId());
			mapping.add(m);
			data.put(sector, mapping);

			return data;

		}

		return null;
	}

	private Map<Department, List<DepartmentSectorIndicatorMapping>> getDepartment(Indicator indicator,
			List<DepartmentSectorIndicatorMapping> mapping) {
		Map<Department, List<DepartmentSectorIndicatorMapping>> data = new HashMap<>();
		Department departmentToReturn = null;
		List<Sector> sectors = indicator.getSc();
		for (Sector sector : sectors) {
			List<Department> dpmtList = sector.getDepartments();
			for (Department department : dpmtList) {
				if (!isMappingPresent(indicator.getId(), sector.getId(), department.getId(), mapping)) {
					departmentToReturn = department;
					DepartmentSectorIndicatorMapping m = new DepartmentSectorIndicatorMapping();
					m.setDepartment(department.getId());
					m.setSector(sector.getId());
					m.setIndicator(indicator.getId());
					mapping.add(m);
					break;
				}
			}
			if (departmentToReturn != null) {
				break;
			}
		}
		data.put(departmentToReturn, mapping);
		return data;
	}

	private Map<Sector, List<DepartmentSectorIndicatorMapping>> getSector(Indicator indicator, int departmentId,
			List<DepartmentSectorIndicatorMapping> mapping) {
		Map<Sector, List<DepartmentSectorIndicatorMapping>> data = new HashMap<>();
		Sector sectorToReturn = null;
		List<Sector> sectors = indicator.getSc();
		for (Sector sector : sectors) {
			if (!isMappingPresent(indicator.getId(), sector.getId(), departmentId, mapping)) {
				sectorToReturn = sector;
				DepartmentSectorIndicatorMapping m = new DepartmentSectorIndicatorMapping();
				m.setDepartment(departmentId);
				m.setSector(sector.getId());
				m.setIndicator(indicator.getId());
				mapping.add(m);
				break;
			}

		}
		if (sectorToReturn == null) {
			return null;
		} else {
			data.put(sectorToReturn, mapping);
			return data;
		}

	}

	private boolean isMappingPresent(int i, int s, int d, List<DepartmentSectorIndicatorMapping> mapping) {

		for (DepartmentSectorIndicatorMapping m : mapping) {
			if (m.getDepartment() == d && m.getSector() == s && m.getIndicator() == i) {
				return true;
			}
		}

		return false;
	}

	@Override
	@Transactional
	public ResponseEntity<String> uploadDataEntryTemplate(MultipartFile file) {

		OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext()
				.getAuthentication();

		UserModel userModel = tokenInfoExtracter.getUserModelInfo(authentication);

		UserDetails userDetails = userDetailsRepository.findOne(userModel.getId());
		Area district = userDetails.getArea();

		XSSFWorkbook workbook = null;
		XSSFSheet sheet = null;
		XSSFRow row = null;
		XSSFCell cell = null;

		Map<String, Integer> sectorMap = sectorJpaRepository.findAll().stream()
				.collect(Collectors.toMap(Sector::getSectorName, Sector::getId));
		Map<String, Integer> indicatorMap = indicatorJpaRepository.findAll().stream()
				.collect(Collectors.toMap(Indicator::getiName, Indicator::getId));
		Map<String, Source> sourceMap = sourceJpaRepository.findAll().stream()
				.collect(Collectors.toMap(Source::getSourceName, s -> s));
		Map<String, Integer> areaMap = areaJpaRepository.findAll().stream()
				.collect(Collectors.toMap(Area::getAreaname, Area::getId));

		try {

			workbook = new XSSFWorkbook(file.getInputStream());

			// checking uuid sheet exist or not
			sheet = workbook.getSheet("UUID_Verify");

			if (sheet == null) {
				workbook.close();
				throw new InvalidFileException(env.getProperty("dataentry.invalid.file.error"));
			}

			row = sheet.getRow(0);
			cell = row.getCell(1);
			String uuidValue = cell.getStringCellValue();

			// checking uuid value for reporting month and year if found allowed
			// to upload
			UUIdGenerator uuidGenerator = uuidGeneratorRepository.findByUuid(uuidValue);

			if (uuidGenerator == null) {
				workbook.close();
				throw new InvalidFileException(env.getProperty("dataentry.invalid.file.error"));
			}

			int columnMaxValue;

			String sector = null;
			String indicator = null;
			String source = null;
			String tp = null;
			String area = null;
			String value = null;
			String target = null;
			String IUS = null;
			int currentRow;
			String errorMessage = null;

			Boolean flag = true;

			sheet = workbook.getSheetAt(0);

			List<Data> newDatasList = new ArrayList<>();

			/**
			 * read department here
			 */
			row = sheet.getRow(2);
			cell = row.getCell(0);
			String dept = cell.getStringCellValue().split(":")[1];
			Department department = departmentRepository.findByDepartmentName(dept);

			/**
			 * sub-group
			 */
			Subgroup subgroup = subgroupJpaRepository.findById(1);

			// row loop
			for (int rows = 5; rows < sheet.getLastRowNum(); rows += 2) {

				row = sheet.getRow(rows);
				columnMaxValue = row.getLastCellNum() - 1;

				sector = null;
				indicator = null;
				source = null;
				tp = null;
				area = null;
				value = null;
				target = null;
				IUS = null;

				for (int cols = 0; cols < row.getLastCellNum(); cols++) {// column
																			// loop

					cell = row.getCell(cols);

					if (cell != null) {

						switch (cols) {

						case 0:
							break;

						// theme--SECTOR
						case 1:
							sector = cell.getStringCellValue();
							break;

						// indicator with target
						case 2:
							indicator = cell.getStringCellValue();
							break;

						// source
						case 3:
							/**
							 * check blank
							 * 
							 * If cell is blank than throw error and let the user know about row and cell
							 * number where it left blank
							 */
							if (CellType.BLANK == cell.getCellTypeEnum()) {
								flag = false;
								errorMessage = setBlackCellErrorMesage(rows, cols, errorMessage);

							} else {
								source = cell.getStringCellValue();
								if (sourceMap.get(source) == null) {
									flag = false;
									errorMessage = setBlackCellErrorMesage(rows, cols, errorMessage);
								}

							}
							break;

						// timeperiod
						case 4:

							/**
							 * check blank
							 * 
							 * If cell is blank then throw error and let the user know about row and cell
							 * number where it left blank
							 */
							if (CellType.BLANK == cell.getCellTypeEnum() || cell.getStringCellValue().equals("")) {
								flag = false;
								errorMessage = setBlackCellErrorMesage(rows, cols, errorMessage);
							} else {
								tp = cell.getStringCellValue();
								if (!checMMMYYYY(tp)) {

									/**
									 * check all the required format of timeperiod
									 */

									// Time period (Monthly:MM-YYYY/MMM-YYYY,
									// Quartely:MM-YYYY:MM-YYYY, Yearly:YYYY,
									// Financial year:YYYY-YYYY:YYYY-YYYY)
									// only above mentioned format is acceptable

									Pattern p = Pattern.compile("\\d{2}-\\d{4}");
									// monthly
									if (p.matcher(tp).matches()) {

										tp = cell.getStringCellValue();
									} else {
										// quarterly
										p = Pattern.compile("\\d{2}-\\d{4}:\\d{2}-\\d{4}");
										Pattern pp = Pattern.compile("\\d{3}-\\d{4}");
										if (p.matcher(tp).matches() || pp.matcher(tp).matches()) {
											tp = cell.getStringCellValue();
										} else {

											// yearly
											p = Pattern.compile("\\d{4}");
											if (p.matcher(tp).matches()) {

												tp = cell.getStringCellValue();
											} else {

												// financial year
												p = Pattern.compile("\\d{4}-\\d{4}");
												if (p.matcher(tp).matches()) {

													tp = cell.getStringCellValue();
													;
												} else {
													// invalid entered pattern
													flag = false;
													errorMessage = setPatternErrorMesage(rows, cols, errorMessage);
												}
											}

										}

									}
								}

							}

							break;

						default:

							row = sheet.getRow(rows);
							cell = row.getCell(cols);
							currentRow = rows;

							/**
							 * get value(eneterd by user)
							 * 
							 * check blank cell first
							 */
							if (CellType.BLANK == cell.getCellTypeEnum()) {
								flag = false;
								errorMessage = setBlackCellErrorMesage(rows, cols, errorMessage);

							} else if (CellType.STRING == cell.getCellTypeEnum()
									&& cell.getStringCellValue().equals("")) {

								flag = false;
								errorMessage = setBlackCellErrorMesage(rows, cols, errorMessage);

							} else if (cols == columnMaxValue) {

								try {
									value = String.valueOf(cell.getNumericCellValue());
								} catch (NumberFormatException | IllegalStateException e) {
									flag = false;
									errorMessage = setBlackCellErrorMesage(rows, cols, errorMessage);
								} catch (Exception e) {
									throw new RuntimeException(e);
								}

							} else {
								value = String.valueOf(cell.getNumericCellValue());
							}
							if (flag) {

								Data data = new Data();
								data.setTrend("up");
								data.setTps("");

								/**
								 * set department
								 */
								data.setDepartment(department);
								/**
								 * set sub-group
								 */
								data.setSubgrp(subgroup);

								if (sector != null)
									data.setSector(new Sector(sectorMap.get(sector)));
								if (indicator != null) {
									data.setIndicator(new Indicator(indicatorMap.get(indicator)));

									/**
									 * set IUS
									 */
									Indicator indc = indicatorJpaRepository.findByIName(indicator);
									IUS = indicator.concat(":").concat(indc.getUnit().getUnitName()).concat(":")
											.concat(indc.getSubgroup().getSubgroupName());
									data.setIus(IUS);
								}

								if (source != null)
									data.setSrc(sourceMap.get(source));

								if (tp != null)
									data.setTp(tp.toUpperCase());

								if (value != null) {
									DecimalFormat df2 = new DecimalFormat("#.##");
									double d = Double.parseDouble(value);
									data.setValue(df2.format(d));
								}

								// now get target value present in next row same
								// column
								rows = rows + 1;
								row = sheet.getRow(rows);
								cell = row.getCell(cols);

								target = String.valueOf((int) cell.getNumericCellValue());

								data.setTarget(target);

								// now get area value
								rows = 4;
								row = sheet.getRow(rows);
								cell = row.getCell(cols);

								if (cols == columnMaxValue) {
									// Avg value is distrct value
									area = district.getAreaname();
									data.setArea(district);
								} else {
									area = String.valueOf(cell.getStringCellValue());
									data.setArea(areaJpaRepository.findOne(areaMap.get(area)));
								}

								// currentValue of running rows
								rows = currentRow;

								/**
								 * checking with sector, indicator,source , time period and area record exist or
								 * not if exist than make previous entry is live as false and enter the newly
								 * added record with is live true.
								 * 
								 */
								if (sector != null && indicator != null && source != null && tp != null) {
									List<Data> dupDatas = dataJpaRepository
											.findBySectorAndIndicatorAndSrcAndTpAndAreaAndIsLiveTrue(data.getSector(),
													data.getIndicator(), data.getSrc(), data.getTp(), data.getArea());

									if (!dupDatas.isEmpty()) {

										dupDatas.forEach(dd -> dd.setIsLive(false));
										dataJpaRepository.save(dupDatas);
									}

								}

								// Set isBaseLine
								List<Data> existingDataList = dataJpaRepository
										.findAllByIndicatorIdAndSectorIdAndAreaIdAndIsLiveTrue(
												indicatorMap.get(indicator), sectorMap.get(sector), areaMap.get(area));

								if (existingDataList.size() > 0) {
									data.setIsBaseline(false);
								} else {
									data.setIsBaseline(true);
								}

								newDatasList.add(data);

							}

						}

					}

				}

			}
			/**
			 * if flag is true than there is no error in the sheet
			 */
			if (flag == true) {
				dataJpaRepository.save(newDatasList);
				workbook.close();
				if (mobileService.createJsonAndZip()) {
					log.info("mobileService.createJsonAndZip() method returned 'true'");
					return new ResponseEntity<String>(
							new Gson().toJson(env.getProperty("dataentry.template.upload.success")), HttpStatus.OK);
				} else {
					log.info("mobileService.createJsonAndZip() method returned 'true'");
					return new ResponseEntity<String>(new Gson().toJson("Sucess, please run the '/zip' URL"),
							HttpStatus.OK);
				}

			} else {
				workbook.close();
				// there is an error in the sheet which is present in
				// errorMessageString
				throw new IncorrectDataFormatException(errorMessage);
			}

		} catch (InvalidFileException e) {
			e.printStackTrace();
			throw new InvalidFileException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	private boolean checMMMYYYY(String tp) {
		try {
			String month = tp.split("-")[0];
			String year = tp.split("-")[1];
			if (month.length() == 3 && year.length() == 4) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			return false;
		}

	}

	private String setPatternErrorMesage(int rows, int cols, String errorMessage) {

		if (errorMessage != null)
			errorMessage = errorMessage.concat(";").concat(
					"Row number : " + (rows + 1) + " Column number: " + (cols + 1) + " TimePeriod Pattern is Invalid.");
		else
			errorMessage = "Row number : " + (rows + 1) + " Column number: " + (cols + 1)
					+ ", please enter valid data.";

		return errorMessage;
	}

	private String setBlackCellErrorMesage(int rows, int cols, String errorMessage) {

		if (errorMessage != null)
			errorMessage = errorMessage.concat(";").concat(
					"Row number : " + (rows + 1) + " Column number: " + (cols + 1) + ", please enter valid data.");
		else
			errorMessage = "Row number : " + (rows + 1) + " Column number: " + (cols + 1)
					+ ", please enter valid data.";

		return errorMessage;
	}

	@Override
	@Transactional
	public ResponseEntity<String> saveTargetData(DataEntryTableModel dataEntryTableModel) {
		
		int size = dataEntryTableModel.getTableHead().size()-1;
		
		List<Map<String, Object>> tableData = dataEntryTableModel.getTableData();
		
		
		List<TargetEventModel> targetEventModels = new ArrayList<>();
		
		for(Map<String, Object> dataMap : tableData) {
			
			for(int i=0;i<size;i++) {
				
				TargetEventModel model = new TargetEventModel();
				model.setAreaId(Integer.valueOf(dataMap.get("areaId").toString()));
				model.setTimePeriodId(Integer.valueOf(dataMap.get("tpId").toString()));
				model.setIndicatorId(Integer.valueOf(dataMap.get("indicatorId".concat("_"+String.valueOf(i+1))).toString()));
				model.setDataValue(dataMap.get("Value".concat("_"+String.valueOf(i+1))).toString());
				model.setTarget(dataMap.get("Target".concat("_"+String.valueOf(i+1))).toString());
				
				if((!model.getDataValue().equals("")) && (!model.getDataValue().isEmpty())) {
					targetEventModels.add(model);
				}
			}
		}
		
		UserModel userModel = tokenInfoExtracter
				.getUserModelInfo((OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication());
		UserDetails userDetail = userDetailsRepository.findByAccount(new Account(userModel.getId()));

		Department department = userDetail.getDepartment();
		
		try {
			
			if(targetEventModels.isEmpty()) {
				throw new SubmissionException(env.getProperty("empty.submission.error"));
			}
		
		TimePeriod timePeriod = timePeriodRepository.findByTimePeriodId(targetEventModels.get(0).getTimePeriodId());

		/**
		 * sub-group
		 */
		Subgroup subgroup = subgroupJpaRepository.findById(1);

		List<Data> datas = new ArrayList<>();
//		List<TargetData> targetDatas = new ArrayList<>();

			for (TargetEventModel targetEventModel : targetEventModels) {

				Data data = new Data();
//				TargetData targetData = new TargetData();

				data.setTrend("up");
				data.setTps("");
				data.setArea(new Area(targetEventModel.getAreaId()));
				data.setDepartment(department);
				data.setIndicator(new Indicator(targetEventModel.getIndicatorId()));

				/**
				 * set IUS
				 */
				Indicator indc = indicatorJpaRepository.findById(targetEventModel.getIndicatorId());
				String IUS = indc.getiName().concat(":").concat(indc.getUnit().getUnitName()).concat(":")
						.concat(indc.getSubgroup().getSubgroupName());
				data.setIus(IUS);

				DecimalFormat df2 = new DecimalFormat("#.##");
				double d = Double.parseDouble(targetEventModel.getDataValue());
				data.setValue(df2.format(d));

				if(!targetEventModel.getTarget().equals("")) {
					DecimalFormat df = new DecimalFormat("#.##");
					double dd = Double.parseDouble(targetEventModel.getTarget());
					data.setTarget(df.format(dd));
				}else {
					data.setTarget(null);
				}

				data.setTimePeriod(timePeriod);
				data.setTp(timePeriod.getShortName());

				data.setSector(indc.getRecSector());
				data.setSrc(indc.getSrc());
				data.setSubgrp(subgroup);

				/**
				 * checking with sector, indicator,source , time period and area record exist or
				 * not if exist than make previous entry is live as false and enter the newly
				 * added record with is live true.
				 * 
				 */
				List<Data> dupDatas = dataJpaRepository.findBySectorAndIndicatorAndSrcAndTpAndAreaAndDepartmentAndIsLiveTrue(
						data.getSector(), data.getIndicator(), data.getSrc(), data.getTp(), data.getArea(),data.getDepartment());

				if (!dupDatas.isEmpty()) {
//					dupDatas.forEach(ddd -> ddd.setIsLive(false));
//					dataJpaRepository.save(dupDatas);
					for(Data ddd: dupDatas) {
						dataJpaRepository.updateDataIsLiveFalse(ddd.getId());
					}
				} 

				data.setPeriodicity(timePeriod.getPeriodicity().getFrequency().toString());

				data.setRank(1);
				datas.add(data);
			}

			if(!datas.isEmpty()) {
				dataJpaRepository.save(datas);
				if (mobileService.createJsonAndZip()) {
					log.info("mobileService.createJsonAndZip() method returned 'true'");
					return new ResponseEntity<String>(
							new Gson().toJson(env.getProperty("dataentry.template.upload.success")), HttpStatus.OK);
				} else {
					log.info("mobileService.createJsonAndZip() method returned 'true'");
					return new ResponseEntity<String>(new Gson().toJson("Sucess, please run the '/zip' URL"),
							HttpStatus.OK);
				}
			}
			return new ResponseEntity<>(new Gson().toJson(env.getProperty("dataentry.submission.success")), HttpStatus.OK);
		}
		catch(SubmissionException e2) {
			throw new SubmissionException(e2.getMessage());
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<FrequencyModel> getFrequencyModel() {

		List<Periodicity> periods = periodicityRepository.findByIsLiveTrue();
		List<FrequencyModel> modelList = new ArrayList<>();

		for (Periodicity p : periods) {
			FrequencyModel m = new FrequencyModel();
			m.setId(p.getId());
			m.setValue(p.getFrequency().toString());
			modelList.add(m);
		}
		return modelList;
	}

	@Override
	public List<FrequencyModel> getTimePeriodModel(Integer id) {

		List<TimePeriod> timePeriods = timePeriodRepository.findByPeriodicityId(id);
		List<FrequencyModel> modelList = new ArrayList<>();

		for (TimePeriod t : timePeriods) {
			FrequencyModel m = new FrequencyModel();
			m.setId(t.getTimePeriodId());
//			if (t.getPeriodicity().getFrequency().equals(Frequency.Quarterly))
//				m.setValue(t.getTimePeriod().split("-")[0].concat("(" + t.getYear().toString() + ")")
//						.concat("-" + t.getTimePeriod().split("-")[1]).concat("(" + t.getYear().toString() + ")"));
//			else
//				m.setValue(t.getTimePeriod().split("-")[0].concat("(" + t.getYear().toString() + ")"));
			m.setValue(t.getShortName());
			modelList.add(m);
		}
		return modelList;

	}

	@Override
	public DataEntryTableModel getData(Integer id) {

		TimePeriod timePeriod = timePeriodRepository.findOne(id);
		String financialYear = timePeriod.getFinancialYear();
		TimePeriod tperiod = timePeriodRepository.findByFinancialYearAndPeriodicityFrequency(financialYear, Frequency.Yearly);
		
		UserModel userModel = tokenInfoExtracter
				.getUserModelInfo((OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication());

		UserDetails userDetails = userDetailsRepository.findByAccount(new Account(userModel.getId()));

		List<TargetData> targets = targetDataRepository.findAllByIsLiveTrue();
		Map<String, String> targetMap = targets.stream()
				.collect(Collectors.toMap(t -> t.getIndicator().getiName().concat("_" + t.getArea().getAreaname())
						.concat("_" + t.getTimePeriod().getTimePeriodId()), TargetData::getTarget));

		Area area = userDetails.getArea();

		// BLOCK
		List<Area> areas = areaJpaRepository.findAllByParentAreaCodeOrderById(area.getCode());

		// indicators list
		// fetch all the indicator mapped to loggedin user department and periodicity
		// wise
		List<Indicator> indicators = indicatorJpaRepository.findByDepartmentIdAndPeriodicityId(
				userDetails.getDepartment().getId(), timePeriod.getPeriodicity().getId());

		DataEntryTableModel model = new DataEntryTableModel();

		List<Map<String, Object>> tableHead = new ArrayList<>();

		Map<String, Object> tableHeadMap = new LinkedHashMap<>();
		tableHeadMap.put("colspan", "1");
		tableHeadMap.put("rowspan", "1");
		tableHeadMap.put("value", "");
		tableHead.add(tableHeadMap);

		for (Indicator ind : indicators) {
			tableHeadMap = new LinkedHashMap<>();
			tableHeadMap.put("colspan", "2");
			tableHeadMap.put("rowspan", "1");
			tableHeadMap.put("value", ind.getiName());
			tableHeadMap.put("id", ind.getId());
			tableHeadMap.put("information", ind.getInformation());
			tableHead.add(tableHeadMap);
		}

		/**
		 * Table-column
		 */
		List<String> tableColumns = new ArrayList<>();
		tableColumns.add("Block");
		for (int i = 0; i < indicators.size(); i++) {
			Integer count = i + 1;
			tableColumns.add("Value".concat("_").concat(count.toString()));
			tableColumns.add("Target".concat("_").concat(count.toString()));
		}

		List<Map<String, Object>> tableData = new ArrayList<>();
		Map<String, Object> tableDataMap = new LinkedHashMap<>();

		for (Area a : areas) {

			tableDataMap = new LinkedHashMap<>();
			tableDataMap.put("Block", a.getAreaname());
			tableDataMap.put("tpId", timePeriod.getTimePeriodId());
			tableDataMap.put("areaId", a.getId());

			for (int i = 0; i < indicators.size(); i++) {
				Indicator indc = indicators.get(i);
				tableDataMap.put("indicatorId".concat("_" + String.valueOf(i + 1)), indc.getId());
				tableDataMap.put("Value".concat("_" + String.valueOf(i + 1)), "");
				String target = targetMap
						.get(indc.getiName().concat("_" + a.getAreaname() + "_" + tperiod.getTimePeriodId()));
				tableDataMap.put("Target".concat("_" + String.valueOf(i + 1)), target);
			}
			tableData.add(tableDataMap);
		}

		model.setTableColumns(tableColumns);
		model.setTableHead(tableHead);
		model.setTableData(tableData);
		return model;
	}

}
