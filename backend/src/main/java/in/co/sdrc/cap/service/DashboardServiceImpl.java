package in.co.sdrc.cap.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import in.co.sdrc.cap.dashboard.model.DashboardAreaModel;
import in.co.sdrc.cap.dashboard.model.DashboardDataModel;
import in.co.sdrc.cap.dashboard.model.DashboardThemeModel;
import in.co.sdrc.cap.dashboard.model.DataForExcel;
import in.co.sdrc.cap.dashboard.model.DepartmentModel;
import in.co.sdrc.cap.dashboard.model.NFHSDataModel;
import in.co.sdrc.cap.dashboard.model.SVGModel;
import in.co.sdrc.cap.dashboard.model.SVGModelForExcel;
import in.co.sdrc.cap.domain.Area;
import in.co.sdrc.cap.domain.Data;
import in.co.sdrc.cap.domain.Department;
import in.co.sdrc.cap.domain.Indicator;
import in.co.sdrc.cap.domain.Sector;
import in.co.sdrc.cap.domain.TargetData;
import in.co.sdrc.cap.domain.TimePeriod;
import in.co.sdrc.cap.exceptions.SubmissionException;
import in.co.sdrc.cap.mobile.model.IndicatorGroupModel;
import in.co.sdrc.cap.model.CardViewTableModel;
import in.co.sdrc.cap.model.TargetDataModel;
import in.co.sdrc.cap.model.TargetModel;
import in.co.sdrc.cap.model.ValueObject;
import in.co.sdrc.cap.repository.AreaJpaRepository;
import in.co.sdrc.cap.repository.DataJpaRepository;
import in.co.sdrc.cap.repository.DepartmentRepository;
import in.co.sdrc.cap.repository.IndicatorJpaRepository;
import in.co.sdrc.cap.repository.NFHSDataRepository;
import in.co.sdrc.cap.repository.SectorJpaRepository;
import in.co.sdrc.cap.repository.SourceJpaRepository;
import in.co.sdrc.cap.repository.TargetDataRepository;
import in.co.sdrc.cap.repository.TimePeriodRepository;
import in.co.sdrc.cap.util.CAPUtil;
import in.co.sdrc.cap.util.Constant;
import in.co.sdrc.cap.util.HeaderFooter;

@Service
public class DashboardServiceImpl implements IDashboardService {

	@Autowired
	private DataJpaRepository dataJpaRepository;

	@Autowired
	private AreaJpaRepository areaJpaRepository;

	@Autowired
	private SectorJpaRepository sectorJpaRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private NFHSDataRepository nFHSDataRepository;
	
	@Autowired
	private IndicatorJpaRepository indicatorJpaRepository;
	
	@Autowired
	private TimePeriodRepository timePeriodRepository;
	
	@Autowired
	private SourceJpaRepository sourceJpaRepository;
	
	@Autowired
	private TargetDataRepository targetDataRepository;
	
	private final Path outputPath = Paths.get("/cap");
	
	@Value("${spring.datasource.url}") private String springDatasourceUrl;
	@Value("${spring.datasource.username}") private String springDatasourceUsername;
	@Value("${spring.datasource.password}") private String springDatasourcePassword;

	@Override
	public ResponseEntity<List<DashboardDataModel>> getFilteredData(String areaCode, int sectorId,Integer departmentId) {
		Area area = areaJpaRepository.findByCode(areaCode);
		
		if(area.getActAreaLevel().getId().intValue()==(Constant.AREA_LEVEL_DISTRICT).intValue()) {
			
			Map<Integer, Indicator> indicators = (Constant.ALL_DEPARTMENT == departmentId)
					? indicatorJpaRepository.findByRecSectorId(sectorId).stream()
							.collect(Collectors.toMap(Indicator::getId, v -> v))
					: ((sectorId == Constant.ALL_THEME)
							? indicatorJpaRepository.findByDepartmentId(departmentId).stream()
									.collect(Collectors.toMap(Indicator::getId, v -> v))
							: indicatorJpaRepository.findByDepartmentIdAndRecSectorId(departmentId, sectorId).stream()
									.collect(Collectors.toMap(Indicator::getId, v -> v)));
			
			return new ResponseEntity<List<DashboardDataModel>>(getDashboardData(areaCode, departmentId, sectorId, indicators), HttpStatus.OK);
		}else {
			List<Data> dataList = null;
			if(sectorId == Constant.ALL_THEME) {
				dataList = dataJpaRepository.findAllByAreaIdAndDepartmentIdAndIsLiveTrue(area.getId(), departmentId);
			}else {
				dataList = dataJpaRepository.findAllBySectorIdAndAreaIdAndDepartmentIdAndIsLiveTrue(sectorId, area.getId(), departmentId);
			}
			List<DashboardDataModel> modelList = new ArrayList<>();
			dataList.stream().forEach(data -> {
				DashboardDataModel model = new DashboardDataModel();
				model.setIndicatorName(data.getIndicator().getiName());
				model.setTarget(data.getTarget());
				model.setTp(data.getTp());
				model.setValue(data.getValue());
				model.setSrc(data.getSrc() != null? data.getSrc().getSourceName(): null);
				model.setBaseline(data.getIsBaseline());
				modelList.add(model);
			});
			return new ResponseEntity<List<DashboardDataModel>>(modelList, HttpStatus.OK);
		}
	}

	private List<DashboardDataModel> getDashboardData(String areaCode, Integer departmentId, Integer sectorId, Map<Integer, Indicator> indicators) {
		List<DashboardDataModel> modeldata = new ArrayList<>();
		 try (Connection conn = DriverManager.getConnection(springDatasourceUrl, springDatasourceUsername, springDatasourcePassword)) {
		 try (CallableStatement function = conn.prepareCall("{ call averagebaselinemrt(?, ?, ?) }")) {
			 function.setString(1, areaCode);
		        function.setInt(2,departmentId);
		        function.setInt(3,sectorId);
		        function.execute();
		        try(ResultSet resultSet =  function.getResultSet()){
		        	 while (resultSet.next()) {
			                String res = resultSet.getString(1);
			                if(res!=null){
			                	 JSONArray jsonObj = new JSONArray(res);
			                	 for(int i=0; i<jsonObj.length(); i++){
			                		 JSONObject obj = (JSONObject)jsonObj.get(i);
			                		
			                		 DashboardDataModel baseLIneDataModel = new DashboardDataModel();
			                		 baseLIneDataModel.setIndicatorName(indicators.get(obj.getInt("indicatorId")).getiName());
			                		 baseLIneDataModel.setTarget(String.format("%.01f", Double.valueOf(obj.getString("target"))));
			                		 baseLIneDataModel.setTp(obj.getString("mrtTp"));
			                		 baseLIneDataModel.setValue(String.format("%.01f", Double.valueOf(obj.getString("baseline"))));
			                		 baseLIneDataModel.setSrc(indicators.get(obj.getInt("indicatorId")).getSrc().getSourceName());
			                		 baseLIneDataModel.setBaseline(true);
			         				
			                		 modeldata.add(baseLIneDataModel);
			                		 
			                		 DashboardDataModel mrtDataModel = new DashboardDataModel();
			                		 mrtDataModel.setIndicatorName(indicators.get(obj.getInt("indicatorId")).getiName());
			                		 mrtDataModel.setTarget(String.format("%.01f", Double.valueOf(obj.getString("target"))));
			                		 mrtDataModel.setTp(obj.getString("mrtTp"));
			                		 mrtDataModel.setValue(String.format("%.01f", Double.valueOf(obj.getString("mrt"))));
			                		 mrtDataModel.setSrc(indicators.get(obj.getInt("indicatorId")).getSrc().getSourceName());
			                		 mrtDataModel.setBaseline(false);
			         				
			                		 modeldata.add(mrtDataModel);
			                	 }
			                }
		        	 }
		        }
		 }
	 }catch (SQLException e) {
           System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
           e.printStackTrace();
       } catch (Exception e) {
           e.printStackTrace();
       }
		return modeldata;
	}

	@Override
	@Cacheable("areaCacheData")
	public ResponseEntity<List<DashboardAreaModel>> getArea() {
		List<DashboardAreaModel> dashboardAreaModelList = new ArrayList<>();

		List<Area> areaList = areaJpaRepository.findAll();
		areaList.stream().forEach(area -> {
			DashboardAreaModel dashboardAreaModel = new DashboardAreaModel();
			dashboardAreaModel.setAreaname(area.getAreaname());
			dashboardAreaModel.setCode(area.getCode());
			dashboardAreaModel.setParentAreaCode(area.getParentAreaCode());
			dashboardAreaModel.setAreaId(area.getId());
			dashboardAreaModel.setAreaLevelName(area.getActAreaLevel().getAreaLevelName());
			dashboardAreaModel.setAreaLevelId(area.getActAreaLevel().getId());
			dashboardAreaModelList.add(dashboardAreaModel);
		});

		return new ResponseEntity<List<DashboardAreaModel>>(dashboardAreaModelList, HttpStatus.OK);
	}

	@Override
	@Cacheable("themeCacheData")
	public ResponseEntity<List<DashboardThemeModel>> getTheme() {
		
		List<DashboardThemeModel> dashboardThemeModelList = new ArrayList<>();
		List<Sector> sectorList = sectorJpaRepository.findAll();
		sectorList.stream().forEach(sector -> {
			DashboardThemeModel dashboardThemeModel = new DashboardThemeModel();
			dashboardThemeModel.setId(sector.getId());
			dashboardThemeModel.setName(sector.getSectorName());
			dashboardThemeModelList.add(dashboardThemeModel);
		});

		return new ResponseEntity<List<DashboardThemeModel>>(dashboardThemeModelList, HttpStatus.OK);
	}
	
	
	@Override
	@Cacheable("deptCacheData")
	public ResponseEntity<List<DepartmentModel>> getDepartment(String typeName) {
		
		Map<Integer, List<Sector>> departments = new HashMap<>();
		List<Sector> sectorList = sectorJpaRepository.findAll();
		
		
		
		List<DepartmentModel> departmentModelList = new ArrayList<>();		
		//all department
		DepartmentModel modelAll = new DepartmentModel();
		
		modelAll.setId(0);
		modelAll.setName(Constant.ALL_DEPARTMENT_NAME);
		
		List<DashboardThemeModel> dashboardThemeModelLists = new ArrayList<>();
		sectorList.stream().forEach(sector -> {
			DashboardThemeModel dashboardThemeModel = new DashboardThemeModel();
			dashboardThemeModel.setId(sector.getId());
			dashboardThemeModel.setName(sector.getSectorName());
			dashboardThemeModelLists.add(dashboardThemeModel);
		});
		if(typeName.equals(Constant.SNAPSHOT_VIEW)) {
			DashboardThemeModel dashboardThemeModel = new DashboardThemeModel();
			dashboardThemeModel.setId(0);
			dashboardThemeModel.setName(Constant.ALL_THEME_NAME);
			dashboardThemeModelLists.add(0, dashboardThemeModel);
		}
		modelAll.setThemes(dashboardThemeModelLists.stream().sorted(Comparator.comparing(DashboardThemeModel::getName))
				.collect(Collectors.toList()));
		
		departmentModelList.add(modelAll);
		
		List<Department> departmentDB = departmentRepository.findAll();
		Map<Integer, Department> deptMap = new HashMap<>();
		departmentDB.stream().forEach(dept->{
			deptMap.put(dept.getId(), dept);
		});
		
		sectorList.stream().forEach(sector -> {
			
			List<Department> departmentList = sector.getDepartments();
			
			departmentList.stream().forEach(department->{				
				List<Sector> sectorListInner = departments.get(department.getId());
				if(sectorListInner == null) {
					sectorListInner = new ArrayList<>();
//					=================================
					Sector se = new Sector();
					se.setId(0);
					se.setSectorName(Constant.ALL_THEME_NAME);
					sectorListInner.add(0, se);
//					=================================
					sectorListInner.add(sector);
				}else if(sectorListInner.size() == 0) {
					sectorListInner.add(sector);
				}else {
					List<Sector> sectorListTemp = sectorListInner.stream()          
			                .filter(data -> (sector.getId() == data.getId()))
			                .collect(Collectors.toList());
					if(sectorListTemp.size() == 0) {
						sectorListInner.add(sector);
					}
				}

				departments.put(department.getId(), sectorListInner);
				
			});
		});
		
		for (Map.Entry<Integer,List<Sector>> entry : departments.entrySet()) { 
			DepartmentModel model = new DepartmentModel();
			model.setId(entry.getKey());
			model.setName(deptMap.get(entry.getKey()).getDepartmentName());
			
			
			List<DashboardThemeModel> dashboardThemeModelList = new ArrayList<>();
			List<Sector> sectorListI = entry.getValue();
			sectorListI.stream().forEach(sector -> {
				DashboardThemeModel dashboardThemeModel = new DashboardThemeModel();
				dashboardThemeModel.setId(sector.getId());
				dashboardThemeModel.setName(sector.getSectorName());
				dashboardThemeModelList.add(dashboardThemeModel);
			});
			
			model.setThemes(dashboardThemeModelList.stream().sorted(Comparator.comparing(DashboardThemeModel::getName))
					.collect(Collectors.toList()));
			
			departmentModelList.add(model);
			
		}
		
		

		return new ResponseEntity<List<DepartmentModel>>(departmentModelList
				.stream().sorted(Comparator.comparing(DepartmentModel::getName))
				.collect(Collectors.toList()), HttpStatus.OK);
	}

	@Override
	public String pdfDownload(List<SVGModel> svgModels, String stateName, String districtName, String blockName,
			HttpServletRequest request) {
		String outputPathPdf = outputPath.toAbsolutePath().toString();

		try {

			String date = new SimpleDateFormat("yyyyMMddHHmmssS").format(new Date());
			String path = outputPathPdf;

			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			outputPathPdf = path + "_" + date + ".pdf";

			Rectangle layout = new Rectangle(PageSize.A4.rotate());
			layout.setBackgroundColor(new BaseColor(242, 190, 170));

			Document document = new Document(layout);//
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputPathPdf));

			String uri = request.getRequestURI();
			String url = request.getRequestURL().toString();
			url = url.replaceFirst(uri, "");

			HeaderFooter headerFooter = new HeaderFooter(url);
			writer.setPageEvent(headerFooter);

			Paragraph areaParagraph = new Paragraph();
			areaParagraph.setAlignment(Element.ALIGN_RIGHT);
			areaParagraph.setSpacingBefore(-20);
			areaParagraph.setSpacingAfter(17);

			Chunk areaChunk = new Chunk("State : " + stateName + ", District : " + districtName + ", Block : " + blockName);
			areaChunk.getFont().setColor(255, 255, 255);
			areaParagraph.add(areaChunk);

			document.open();
			document.add(areaParagraph);

			byte[] svgImageBytes = Base64.decodeBase64((svgModels.get(0).getValue()).split(",")[1]);
			Image svgImage = Image.getInstance(svgImageBytes);

			System.out.println(layout.getWidth() + "-" + layout.getHeight());
			svgImage.scaleAbsolute (layout.getWidth(), layout.getHeight()/3f);
			svgImage.setAbsolutePosition(0, 345);

			document.add(svgImage);
			
			//theme
			svgImageBytes = Base64.decodeBase64((svgModels.get(1).getValue()).split(",")[1]);
			svgImage = Image.getInstance(svgImageBytes);

			svgImage.scaleAbsolute ((layout.getWidth() * 20f)/100f , layout.getHeight() - (layout.getHeight()/2f));
			svgImage.setAbsolutePosition(0, 40);

			document.add(svgImage);
			
			//chart
			svgImageBytes = Base64.decodeBase64((svgModels.get(2).getValue()).split(",")[1]);
			svgImage = Image.getInstance(svgImageBytes);

			svgImage.scaleAbsolute ((layout.getWidth() * 78f)/100f , layout.getHeight() - (layout.getHeight()/2f));
			svgImage.setAbsolutePosition(180, 40);

			document.add(svgImage);

			document.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return outputPathPdf;
	}

	@Override
	public String excelDownload(SVGModelForExcel svgModel, HttpServletRequest request) {
		
		String outputPathExcel = outputPath.toAbsolutePath().toString();
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			URL url = loader.getResource("images/cap-header.jpg");
			String path1 = url.getPath().replaceAll("%20", " ");
			File file = new File(path1);
			BufferedImage bImage = ImageIO.read(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(bImage, "jpg", bos);

			byte[] headerImgBytes = bos.toByteArray();
			String date = new SimpleDateFormat("yyyyMMddHHmmssS").format(new Date());
			String path = outputPathExcel;
			outputPathExcel = path + "_" + date + ".xlsx";

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("cap");
			XSSFCellStyle cellstyleMiddle = CAPUtil.getStyleForLeftMiddle(workbook);
			XSSFCellStyle cellstyleFont = CAPUtil.getStyleForFont(workbook);
			XSSFRow row = null;
			XSSFCell cell = null;
			int rowNum = 0;
			int cellNum = 0;
			row = sheet.createRow(rowNum);
			cell = row.createCell(cellNum);

			rowNum = insertimage(rowNum, headerImgBytes, workbook, sheet);

			rowNum++;
			row = sheet.createRow(rowNum);
			cell = row.createCell(cellNum);
			cell.setCellValue("District Name : " + svgModel.getDistrictName());
			sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, cellNum, cellNum + 18));
			cell.setCellStyle(cellstyleFont);
			
			rowNum++;
			row = sheet.createRow(rowNum);
			cell = row.createCell(cellNum);
			cell.setCellValue("Block Name : " + svgModel.getBlockName());
			sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, cellNum, cellNum + 18));
			cell.setCellStyle(cellstyleFont);
			
			rowNum += 2;
			


			//Set theme name
			row = sheet.createRow(rowNum);
			cell = row.createCell(cellNum);
			cell.setCellValue(svgModel.getThemeName().toUpperCase());
			sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, cellNum, cellNum + 18));
			cell.setCellStyle(cellstyleMiddle);
			//set theme name end
				
			
			rowNum += 2;
			cellNum = 0;

			
			//outcome data
			byte[] svgImageBytes = Base64.decodeBase64(((String) svgModel.getSvg().get(0)).split(",")[1]);
			row = sheet.createRow(rowNum);	
			cellNum = 0;
			row = sheet.createRow(rowNum);
			cell = row.createCell(cellNum);			
			
			int rownum = insertimageForOutcomeData(rowNum, svgImageBytes, workbook, sheet);
			
			setDataValuesForOutcome(rowNum, sheet, 11, workbook, svgModel);
			
			
			
			//chart data
			svgImageBytes = Base64.decodeBase64(((String) svgModel.getSvg().get(1)).split(",")[1]);
			row = sheet.createRow(rowNum);	
			cellNum = 0;
			row = sheet.createRow(rowNum);
			cell = row.createCell(cellNum);
			
			rowNum += 12;
			rownum = insertimage(rowNum, svgImageBytes, workbook, sheet);

			setDataValues(rowNum, sheet, 11, workbook, svgModel);
			
			rowNum += rownum;

			FileOutputStream fileOutputStream = new FileOutputStream(new File(outputPathExcel));
			workbook.write(fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outputPathExcel;
	}
	
	
	private void setDataValuesForOutcome(int rowNum, XSSFSheet sheet, int cellNum,
			XSSFWorkbook workbook, SVGModelForExcel svgModel) {

		XSSFRow row = null;
		XSSFCell cell = null;
		int cellNumTemp = cellNum;

		XSSFCellStyle indicatorStyle = CAPUtil.getIndicatorCell(workbook);
		XSSFCellStyle bold = CAPUtil.bold(workbook);
		boolean flag = true;
		for(int i = 0; i < svgModel.getOutcome().size();i++) {
			
			if(i == 0 && flag) {
				rowNum++;
				int cellnum = cellNumTemp;
				row = sheet.createRow(rowNum);
				cell = row.createCell(cellnum);
				cell.setCellValue("Outcome indicator name");
				cell.setCellStyle(bold);
				
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue("Value");
				cell.setCellStyle(bold);
				
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue("Unit");
				cell.setCellStyle(bold);
				sheet.setColumnWidth(cellnum, 4000);
				
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue("Source");
				cell.setCellStyle(bold);
				
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue("Time period");
				sheet.setColumnWidth(cellnum, 4000);
				cell.setCellStyle(bold);
				
				flag = false;
				i--;
			}else {
				DataForExcel data = svgModel.getOutcome().get(i);  
				rowNum++;
				int cellnum = cellNumTemp;
				row = sheet.createRow(rowNum);
				cell = row.createCell(cellnum);
				cell.setCellValue(data.getName());
				cell.setCellStyle(indicatorStyle);
				
				
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue(data.getBaseline());
				cell.setCellStyle(indicatorStyle);
				
				
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue(data.getUnit());
				cell.setCellStyle(indicatorStyle);
				
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue(data.getSrc());
				cell.setCellStyle(indicatorStyle);
				
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue(data.getTp());
				cell.setCellStyle(indicatorStyle);
			}
			

			
			
		}
		


	}
	

	private void setDataValues(int rowNum, XSSFSheet sheet, int cellNum,
			XSSFWorkbook workbook, SVGModelForExcel svgModel) {

		XSSFCellStyle indicatorStyle = CAPUtil.getIndicatorCell(workbook);
		XSSFCellStyle bold = CAPUtil.bold(workbook);
		XSSFRow row = null;
		XSSFCell cell = null;
		int cellNumTemp = cellNum;

		row = sheet.createRow(rowNum);
		cell = row.createCell(cellNum);
		cell.setCellValue("Indicator name");
		sheet.setColumnWidth(cellNum, 13000);
		cell.setCellStyle(bold);
		
		cellNum++;
		cell = row.createCell(cellNum);
		cell.setCellValue("Baseline");
		cell.setCellStyle(bold);
		
		
		cellNum++;
		cell = row.createCell(cellNum);
		cell.setCellValue("MRT");
		cell.setCellStyle(bold);
		
		
		cellNum++;
		cell = row.createCell(cellNum);
		cell.setCellValue("Target");
		cell.setCellStyle(bold);
		
		

		
		for (DataForExcel data: svgModel.getDataForExcel()) {
			rowNum++;
			int cellnum = cellNumTemp;
			row = sheet.createRow(rowNum);
			cell = row.createCell(cellnum);
			cell.setCellValue(data.getName());
			cell.setCellStyle(indicatorStyle);
			
			cellnum++;
			cell = row.createCell(cellnum);
			cell.setCellValue(data.getBaseline());
			cell.setCellStyle(indicatorStyle);
			cellnum++;
			cell = row.createCell(cellnum);
			cell.setCellValue(data.getMrt());
			cell.setCellStyle(indicatorStyle);
			
			cellnum++;
			cell = row.createCell(cellnum);
			cell.setCellValue(data.getTarget());
			cell.setCellStyle(indicatorStyle);
			

		}

	}

	// insert image in excel file
	private int insertimage(int rowNum, byte[] imageBytes, XSSFWorkbook xssfWorkbook, XSSFSheet sheet) {
		Integer size = null;
		try {
			int pictureIdx = xssfWorkbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
			CreationHelper helper = xssfWorkbook.getCreationHelper();
			Drawing<?> drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			anchor.setCol1(0);
			anchor.setCol2(11);
			anchor.setRow1(rowNum);
			anchor.setRow2(rowNum + 10);
			Picture pict = drawing.createPicture(anchor, pictureIdx);
			if (pict.getImageDimension().getHeight() <= 100) {
				anchor.setCol2(19);
				anchor.setRow2(4);
				size = 4;
			} else if (pict.getImageDimension().getHeight() < 150) {
				pict.resize(1.2, 0.5);
				size = 12;
			} else if (pict.getImageDimension().getHeight() > 150 && pict.getImageDimension().getHeight() < 300) {
				pict.resize(1.2, 1.0);
				size = 18;
			} else if (pict.getImageDimension().getHeight() > 300) {
//				pict.resize(1.65);
				size = 24;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return size.intValue();
	}
	
	
	private int insertimageForOutcomeData(int rowNum, byte[] imageBytes, XSSFWorkbook xssfWorkbook, XSSFSheet sheet) {
		Integer size = null;
		try {
			int pictureIdx = xssfWorkbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
			CreationHelper helper = xssfWorkbook.getCreationHelper();
			Drawing<?> drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			anchor.setCol1(0);
			anchor.setCol2(11);
			anchor.setRow1(rowNum);
			anchor.setRow2(rowNum + 7);
			Picture pict = drawing.createPicture(anchor, pictureIdx);
			if (pict.getImageDimension().getHeight() <= 100) {
				anchor.setCol2(19);
				anchor.setRow2(4);
				size = 4;
			} else if (pict.getImageDimension().getHeight() < 150) {
				pict.resize(1.2, 0.5);
				size = 12;
			} else if (pict.getImageDimension().getHeight() > 150 && pict.getImageDimension().getHeight() < 300) {
				pict.resize(1.2, 1.0);
				size = 18;
			} else if (pict.getImageDimension().getHeight() > 300) {
//				pict.resize(1.65);
				size = 24;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return size.intValue();
	}

	@Override
	@Cacheable("nfhsCacheData")
	public List<Map<Object, List<NFHSDataModel>>> getNFHSData() {
		List<Map<Object, List<NFHSDataModel>>> nfhsDataMap = new ArrayList<>();
		nfhsDataMap.add(nFHSDataRepository.findAll().stream()
				.map(v->{
					NFHSDataModel nfhsDataModel = new NFHSDataModel();
					nfhsDataModel.setArea(v.getArea().getAreaname());
					nfhsDataModel.setImage(v.getImage());
					nfhsDataModel.setName(v.getName());
					nfhsDataModel.setSrc(v.getSrc());
					nfhsDataModel.setTp(v.getTp());
					nfhsDataModel.setValue(v.getValue());
					
					return nfhsDataModel;
				}).sorted(Comparator.comparing(NFHSDataModel::getArea).reversed())
				.collect(Collectors.groupingBy(NFHSDataModel::getArea)));
		 
		 return nfhsDataMap;
	}

	@Override
	public Map<String, List<IndicatorGroupModel>> getCardViewData(String areaCode, Integer departmentId, Integer sectorId, Integer areaLevelId){
		List<Indicator> indicators = null;
	
		
		if(departmentId == Constant.ALL_DEPARTMENT && sectorId == Constant.ALL_THEME) {
			indicators = indicatorJpaRepository.findAll();
		} else if(departmentId == Constant.ALL_DEPARTMENT && sectorId != null) {
			indicators = indicatorJpaRepository.findByRecSectorId(sectorId);
		}  else if(departmentId != Constant.ALL_DEPARTMENT && sectorId == Constant.ALL_THEME) {
			indicators = indicatorJpaRepository.findByDepartmentId(departmentId);
		} else {
			indicators = indicatorJpaRepository.findByDepartmentIdAndRecSectorId(departmentId, sectorId);
		}
		
		Map<Integer, Indicator> indicatorsMap = indicators.stream().collect(Collectors.toMap(Indicator::getId, v->v));
//		Map<Integer, Source> sourceMap = sourceJpaRepository.findAll().stream().collect(Collectors.toMap(k->k.getId(), v->v));
//		Map<Integer, Department> deptMap = departmentRepository.findAll().stream().collect(Collectors.toMap(k->k.getId(), v->v));
//		Map<Integer, Sector> sectorMap = sectorJpaRepository.findAll().stream().collect(Collectors.toMap(k->k.getId(), v->v));
		
		List<Object[]> dataList = null;
		if(areaLevelId == Constant.AREA_LEVEL_STATE) {
			if(departmentId != Constant.ALL_DEPARTMENT && sectorId != Constant.ALL_THEME) {
				dataList = dataJpaRepository.getAllBlockDataForState(Constant.AREA_LEVEL_BLOCK, departmentId, sectorId);
			}else if(departmentId != Constant.ALL_DEPARTMENT && sectorId == Constant.ALL_THEME) {
				dataList = dataJpaRepository.getAllBlockDataDeptForState(Constant.AREA_LEVEL_BLOCK, departmentId);
			}else {
				dataList = dataJpaRepository.getDataForState(Constant.AREA_LEVEL_BLOCK, indicators.stream().map(v->v.getId()).collect(Collectors.toList()));
			}
		}else if(areaLevelId == Constant.AREA_LEVEL_DISTRICT) {
			if(departmentId != Constant.ALL_DEPARTMENT && sectorId != Constant.ALL_THEME) {
				dataList = dataJpaRepository.getAllBlockData(areaCode, departmentId, sectorId);
			}else if(departmentId != Constant.ALL_DEPARTMENT && sectorId == Constant.ALL_THEME) {
				dataList = dataJpaRepository.getAllBlockDataDept(areaCode, departmentId);
			}else {
				dataList = dataJpaRepository.getData(areaCode, indicators.stream().map(v->v.getId()).collect(Collectors.toList()));
			}
		}else {
			dataList = dataJpaRepository.getBlockData(areaCode, departmentId, sectorId);
		}
		
		return dataList.stream().map(v->{
					IndicatorGroupModel indicatorGroupModel = new IndicatorGroupModel();
					indicatorGroupModel.setIndicatorId((int)v[0]);
					indicatorGroupModel.setIndicatorName(indicatorsMap.get((int)v[0]).getiName());
					indicatorGroupModel.setIndicatorValue(String.format("%.01f", v[1]).toString());
//					indicatorGroupModel.setSource(sourceMap.get(v[2]).getSourceName());
					indicatorGroupModel.setSource(indicatorsMap.get((int)v[0]).getSrc().getSourceName());
				
					indicatorGroupModel.setTimeperiod(v[3].toString());
					indicatorGroupModel.setTimeperiodId((int)v[5]);
					
					indicatorGroupModel.setPeriodicity(v[4].toString());
					indicatorGroupModel.setDepartmentId(indicatorsMap.get((int)v[0]).getDepartment().getId());
					indicatorGroupModel.setDepartmentName(indicatorsMap.get((int)v[0]).getDepartment().getDepartmentName());
					indicatorGroupModel.setSectorId(indicatorsMap.get((int)v[0]).getRecSector().getId());
					indicatorGroupModel.setSectorName(indicatorsMap.get((int)v[0]).getRecSector().getSectorName());
					indicatorGroupModel.setChartsAvailable(Arrays.asList(Constant.CARD));
					indicatorGroupModel.setUnit(Constant.PERCENT);
					indicatorGroupModel.setAlign(Constant.COL_MD_4);
					indicatorGroupModel.setChartGroup(Constant.INDICATORS);
					return indicatorGroupModel;
				}).collect(Collectors.groupingBy(IndicatorGroupModel::getChartGroup));
		
	}

	@Override
	public List<ValueObject> getCardViewTimePeriods(String parentAreaCode, Integer indicatorId, 
			Integer departmentId, Integer sectorId, Integer areaLevelId) {
		
		List<Object[]> timePeriods = null;
		if(areaLevelId == Constant.AREA_LEVEL_STATE) {
			if(departmentId == Constant.ALL_DEPARTMENT && sectorId == Constant.ALL_THEME) {
				timePeriods= dataJpaRepository.getTimePeriodsWTSectorDeptForState(Constant.AREA_LEVEL_BLOCK, indicatorId);
			} else if(departmentId == Constant.ALL_DEPARTMENT && sectorId != Constant.ALL_THEME) {
				timePeriods = dataJpaRepository.getTimePeriodsSectorForState(Constant.AREA_LEVEL_BLOCK, indicatorId, sectorId);
			} else if(departmentId != Constant.ALL_DEPARTMENT && sectorId == Constant.ALL_THEME){
				timePeriods = dataJpaRepository.getTimePeriodsDeptForState(Constant.AREA_LEVEL_BLOCK, indicatorId, departmentId);
			} else {
				timePeriods = dataJpaRepository.getTimePeriodsForState(Constant.AREA_LEVEL_BLOCK, indicatorId, departmentId, sectorId);
			}
		}else {
			if(departmentId == Constant.ALL_DEPARTMENT && sectorId == Constant.ALL_THEME) {
				timePeriods= dataJpaRepository.getTimePeriodsWTSectorDept(parentAreaCode, indicatorId);
			} else if(departmentId == Constant.ALL_DEPARTMENT && sectorId != Constant.ALL_THEME) {
				timePeriods = dataJpaRepository.getTimePeriodsSector(parentAreaCode, indicatorId, sectorId);
			} else if(departmentId != Constant.ALL_DEPARTMENT && sectorId == Constant.ALL_THEME){
				timePeriods = dataJpaRepository.getTimePeriodsDept(parentAreaCode, indicatorId, departmentId);
			} else {
				timePeriods = dataJpaRepository.getTimePeriods(parentAreaCode, indicatorId, departmentId, sectorId);
			}
		}
		
		return timePeriods.stream().map(v->{
					ValueObject object = new ValueObject();
					object.setId((int)v[0]);
					object.setTpName(v[1].toString());
					
					return object;
				}).sorted(Comparator.comparingInt(ValueObject::getId).reversed()).collect(Collectors.toList());
	}
	
	@Override
	@Cacheable("cardViewTableData")
	public List<CardViewTableModel> getCardViewTableData(String parentAreaCode, Integer indicatorId, 
			Integer timePeridId,Integer areaLevelId, Integer departmentId, Integer sectorId) {
		List<Data> dataList = null;
		if(areaLevelId == Constant.AREA_LEVEL_STATE) {
			if(departmentId == Constant.ALL_DEPARTMENT && sectorId == Constant.ALL_THEME) {
				dataList= dataJpaRepository.getCardViewTableDataWTSectorDeptForState(Constant.AREA_LEVEL_BLOCK, indicatorId, timePeridId);
			} else if(departmentId == Constant.ALL_DEPARTMENT && sectorId != Constant.ALL_THEME) {
				dataList = dataJpaRepository.getCardViewTableDataSectorForState(Constant.AREA_LEVEL_BLOCK, indicatorId, timePeridId, sectorId);
			} else if(departmentId != Constant.ALL_DEPARTMENT && sectorId == Constant.ALL_THEME){
				dataList = dataJpaRepository.getCardViewTableDataDeptForState(Constant.AREA_LEVEL_BLOCK, indicatorId, timePeridId, departmentId);
			} else {
				dataList = dataJpaRepository.getCardViewTableBlockData(Constant.AREA_LEVEL_BLOCK, indicatorId, timePeridId, departmentId, sectorId);
			}
			
			Map<String, Area> areaMap = areaJpaRepository.findAll().stream().collect(Collectors.toMap(Area::getCode, v->v));
			
			Map<String, Double> districtData = dataList.stream().collect(Collectors.groupingBy(k->k.getArea().getParentAreaCode(), 
					Collectors.averagingDouble(v->Double.valueOf(v.getValue()))));
			
			List<CardViewTableModel> cardViewTableModels = new ArrayList<>();
			
			double averageValue = districtData.entrySet().stream().mapToDouble(v->Double.valueOf(v.getValue())).reduce(0, Double::sum)/districtData.size();
			for (Entry<String, Double> v : districtData.entrySet()) {
				
				CardViewTableModel viewTableModel = new CardViewTableModel();
				viewTableModel.setArea(areaMap.get(v.toString().split("=")[0]).getAreaname());
				viewTableModel.setId(areaMap.get(v.toString().split("=")[0]).getId());
				viewTableModel.setIndicatorId(indicatorId);
				viewTableModel.setTpId(timePeridId);
				viewTableModel.setValue(String.format("%.01f", Double.valueOf(v.toString().split("=")[1])).toString());
				String getIndex = getIndex(v.toString().split("=")[1], averageValue);
				viewTableModel.setCss(getIndex.split("_")[0]);
				viewTableModel.setIndex(String.format("%.01f", Double.valueOf(getIndex.split("_")[1])).toString());
				
				cardViewTableModels.add(viewTableModel);
			}
			
			return cardViewTableModels;
			
		}else {
			if(departmentId == Constant.ALL_DEPARTMENT && sectorId == Constant.ALL_THEME) {
				dataList= dataJpaRepository.getCardViewTableDataWTSectorDept(parentAreaCode, indicatorId, timePeridId);
			} else if(departmentId == Constant.ALL_DEPARTMENT && sectorId != Constant.ALL_THEME) {
				dataList = dataJpaRepository.getCardViewTableDataSector(parentAreaCode, indicatorId, timePeridId, sectorId);
			} else if(departmentId != Constant.ALL_DEPARTMENT && sectorId == Constant.ALL_THEME){
				dataList = dataJpaRepository.getCardViewTableDataDept(parentAreaCode, indicatorId, timePeridId, departmentId);
			} else {
				dataList = dataJpaRepository.getCardViewTableData(parentAreaCode, indicatorId, timePeridId, departmentId, sectorId);
			}
			
			double averageValue = dataList.stream().mapToDouble(v->Double.valueOf(v.getValue())).reduce(0, Double::sum)/dataList.size();
			return 	dataList.stream().map(v->{
						CardViewTableModel viewTableModel = new CardViewTableModel();
						viewTableModel.setArea(v.getArea().getAreaname());
						viewTableModel.setId(v.getArea().getId());
						viewTableModel.setIndicatorId(indicatorId);
						viewTableModel.setTpId(timePeridId);
						viewTableModel.setValue(v.getValue());
						String getIndex = getIndex(v.getValue(), averageValue);
						viewTableModel.setCss(getIndex.split("_")[0]);
						viewTableModel.setIndex(String.format("%.01f", Double.valueOf(getIndex.split("_")[1])).toString());
						
						return viewTableModel;
					}).collect(Collectors.toList());
		}
	}

	private String getIndex(String value, double averageValue) {
		Double avgValue = (Double.valueOf(value))/averageValue;
		String cssIndex = "";
		if(avgValue<80) {
			cssIndex = Constant.COLOR_RED+"_"+avgValue;
		}else if (avgValue>=80 && avgValue<=120) {
			cssIndex = Constant.COLOR_YELLOW+"_"+avgValue;
		}else {
			cssIndex = Constant.COLOR_GREEN+"_"+avgValue;
		}
		return cssIndex;
	}
	
	@Override
	public Map<String, Object> getCardViewTableDataWithTP(String parentAreaCode, Integer indicatorId,
			Integer timePeridId1, Integer areaLevelId, Integer departmentId, Integer sectorId) {
		List<Data> dataList = null;
		
		if(departmentId == Constant.ALL_DEPARTMENT && sectorId == Constant.ALL_THEME) {
			dataList = dataJpaRepository.getCardViewTableDataTP(parentAreaCode, indicatorId);
		} else if(departmentId == Constant.ALL_DEPARTMENT && sectorId != Constant.ALL_THEME) {
			dataList = dataJpaRepository.getCardViewTableDataTPSector(parentAreaCode, indicatorId, sectorId);
		} else if(departmentId != Constant.ALL_DEPARTMENT && sectorId == Constant.ALL_THEME){
			dataList = dataJpaRepository.getCardViewTableDataTPDept(parentAreaCode, indicatorId, departmentId);
		} else {
			dataList = dataJpaRepository.getCardViewTableDataTPData(parentAreaCode, indicatorId, departmentId, sectorId);
		}
		

		return dataList.stream().collect(Collectors.groupingBy(Data::getTp, Collectors.collectingAndThen(Collectors.toList(), v -> {
			double averageValue = v.stream().collect(Collectors.averagingDouble(l->Double.valueOf(l.getValue())));
			return v.stream().map(m->{
				CardViewTableModel viewTableModel = new CardViewTableModel();
				viewTableModel.setArea(m.getArea().getAreaname());
				viewTableModel.setId(m.getArea().getId());
				viewTableModel.setIndicatorId(m.getIndicator().getId());
				viewTableModel.setTpId(m.getTimePeriod().getTimePeriodId());
				viewTableModel.setValue(m.getValue());
				String getIndex = getIndex(m.getValue(), averageValue);
				viewTableModel.setCss(getIndex.split("_")[0]);
				viewTableModel.setIndex(String.format("%.01f", Double.valueOf(getIndex.split("_")[1])).toString());
				
				return viewTableModel;
			});
		})));
	}

	@Override
	@Cacheable("financialYearCacheData")
	public List<ValueObject> getFinancialYear() {
		return timePeriodRepository.getTimeperiods(new Date(),Constant.YEARLY_PERIODICITY)
				.stream()
				.map(v->{
					ValueObject object = new ValueObject();
					object.setId(v.getTimePeriodId());
					object.setTpName(v.getShortName());
					object.setFinancialYear(v.getFinancialYear());
					return object;
				})
				.collect(Collectors.toList());
	}
	
	@Override
	@Cacheable("targetCacheData")
	public TargetModel getTargetData(String parentAreaCode, Integer sectorId, Integer timePeridId) {
		
		Map<String, List<Object[]>> indicators = indicatorJpaRepository.getIndicators(parentAreaCode, sectorId)
				.stream().collect(Collectors.groupingBy(v->(String)v[2], Collectors.toList())).entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new)); 
		
	
		List<Map<String, Object>> list = new ArrayList<>();
		
		Map<String, Object> map = null;

		for(Entry<String, List<Object[]>> obj: indicators.entrySet()) {
			map = new LinkedHashMap<String, Object>();
			map.put("block", obj.getKey());
			
			for (Object[] objects : indicators.get(obj.getKey())) {
				TargetDataModel dataModel = new TargetDataModel();
				dataModel.setIndicatorId((Integer)objects[0]);
				dataModel.setAreaId((Integer)objects[3]);
				dataModel.setTpId(timePeridId);
				dataModel.setValue("");
				dataModel.setType(Constant.TEXT_TYPE);
				dataModel.setFiledType(Constant.TEXT_TYPE);
				map.put(objects[1].toString(), dataModel);
			}
			
			list.add(map);
		}
		TargetModel targetModel = new TargetModel();
		targetModel.setParentAreaCode(parentAreaCode);
		targetModel.setSectorId(sectorId);
		targetModel.setTimePeridId(timePeridId);
		targetModel.setTargetDatas(list);
		
		return targetModel;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public String saveTargetData(TargetModel targetModel) {
		try {
			List<Map<String, Object>> targetModelObj = targetModel.getTargetDatas();
			
			List<Object> targetDatas = new ArrayList<>();
			for (Map<String, Object> map : targetModelObj) {
				for (Entry<String, Object> object : map.entrySet()) {
					if(!(map.get(object.getKey()) instanceof String)) {
						targetDatas.add(map.get(object.getKey()));
					}
				}
			}
			List<Integer> indicators = new ArrayList<>();
			List<Integer> areaIds = new ArrayList<>();
			List<TargetData> taDatas = new ArrayList<>();
			
			boolean flag = true;
			
			for (Object object : targetDatas) {
				TargetData data  = new TargetData();
				Map<String, Object> map = (LinkedHashMap<String, Object>)object;
				if(!map.get("value").equals("")) {
					data.setTarget((map.get("value")).toString());
					data.setArea(new Area(Integer.valueOf((map.get("areaId")).toString())));
					data.setIndicator(new Indicator(Integer.valueOf((map.get("indicatorId")).toString())));
					data.setTimePeriod(new TimePeriod(Integer.valueOf((map.get("tpId")).toString())));
					data.setLive(true);
					
					indicators.add(data.getIndicator().getId());
					areaIds.add(data.getArea().getId());
					taDatas.add(data);
				}else {
					flag = false;
					break;
				}
			}
			
			if(flag == true) {
				targetDataRepository.updateTargetData(indicators, areaIds,targetModel.getTimePeridId()).stream().forEach(v->v.setLive(false));
				targetDataRepository.flush();
				targetDataRepository.save(taDatas);
				return Constant.SUCCESS_MESSAGE;
			}else {
				return Constant.FAILED_MESSAGE;
			}
		}catch (DataIntegrityViolationException e) {
			throw new SubmissionException(Constant.DUPLICATE_ENTRY_MESSAGE);
		}
		catch (Exception e) {
			return Constant.FAILED_MESSAGE;
		}
		
	}
}

